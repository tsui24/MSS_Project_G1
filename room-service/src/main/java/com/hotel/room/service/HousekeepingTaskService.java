package com.hotel.room.service;

import com.hotel.room.dto.HousekeepingTaskRequest;
import com.hotel.room.dto.HousekeepingTaskResponse;
import com.hotel.room.dto.ChecklistStepRequest;
import com.hotel.room.entity.HousekeepingTask;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;
import com.hotel.room.exception.DuplicateResourceException;
import com.hotel.room.exception.ResourceNotFoundException;
import com.hotel.room.repository.HousekeepingTaskRepository;
import com.hotel.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

@Service
public class HousekeepingTaskService {
    private static final Set<String> STATUSES = Set.of("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED");
    private final HousekeepingTaskRepository tasks;
    private final RoomRepository rooms;

    public HousekeepingTaskService(HousekeepingTaskRepository tasks, RoomRepository rooms) {
        this.tasks = tasks; this.rooms = rooms;
    }

    @PostConstruct
    public void reconcileActiveTaskRoomStatuses() {
        for (HousekeepingTask task : tasks.findByStatusIn(List.of("PENDING", "IN_PROGRESS"))) {
            Room room = task.getRoom();
            if (task.getPreviousRoomStatus() == null) task.setPreviousRoomStatus(room.getStatus().name());
            if ("IN_PROGRESS".equals(task.getStatus()) && task.getStartedAt() == null) {
                task.setStartedAt(task.getCreatedAt());
            }
            // An occupied room belongs to the booking lifecycle. Housekeeping must never
            // make an in-house guest's room bookable or unavailable by overwriting it.
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                tasks.save(task);
                continue;
            }
            RoomStatus target = targetRoomStatus(task, task.getStatus());
            if (room.getStatus() != target) {
                room.setStatus(target);
                rooms.save(room);
            }
            tasks.save(task);
        }
    }

    public List<HousekeepingTaskResponse> findAll(Long staffId) {
        List<HousekeepingTask> result = staffId == null
                ? tasks.findAllByOrderByCreatedAtDesc()
                : tasks.findByStaffIdOrderByCreatedAtDesc(staffId);
        return result.stream().map(HousekeepingTaskResponse::new).toList();
    }

    public HousekeepingTaskResponse get(Long id) { return new HousekeepingTaskResponse(find(id)); }

    public HousekeepingTaskResponse create(HousekeepingTaskRequest request) {
        if (tasks.existsByRoomIdAndStatusIn(request.getRoomId(), List.of("PENDING", "IN_PROGRESS"))) {
            throw new DuplicateResourceException("Room already has an active housekeeping task");
        }
        Room room = rooms.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + request.getRoomId()));
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new IllegalArgumentException("Cannot assign housekeeping while room is OCCUPIED");
        }
        HousekeepingTask task = new HousekeepingTask();
        task.setRoom(room); task.setStaffId(request.getStaffId());
        task.setAssigneeName(request.getAssigneeName()); task.setTaskType(request.getTaskType());
        task.setPreviousRoomStatus(room.getStatus().name());
        task.setCompletedSteps(0);
        room.setStatus("CLEANING".equals(request.getTaskType()) ? RoomStatus.DIRTY : RoomStatus.MAINTENANCE);
        rooms.save(room);
        return new HousekeepingTaskResponse(tasks.save(task));
    }

    @Transactional
    public HousekeepingTaskResponse updateStatus(Long id, String status) {
        String normalized = status.toUpperCase();
        if (!STATUSES.contains(normalized)) throw new IllegalArgumentException("Invalid task status: " + status);
        HousekeepingTask task = find(id);
        validateTransition(task.getStatus(), normalized);
        if (task.getStatus().equals(normalized)) {
            return new HousekeepingTaskResponse(task);
        }
        if (task.getRoom().getStatus() == RoomStatus.OCCUPIED
                && !normalized.equals(task.getStatus()) && !"CANCELLED".equals(normalized)) {
            throw new IllegalArgumentException("Cannot update housekeeping task while room is OCCUPIED");
        }
        if ("COMPLETED".equals(normalized)) {
            int requiredSteps = HousekeepingChecklist.steps(task.getTaskType()).size();
            if (task.getCompletedSteps() != requiredSteps) {
                throw new IllegalArgumentException("Complete all " + requiredSteps
                        + " checklist steps before completing the task");
            }
        }
        task.setStatus(normalized);
        if ("IN_PROGRESS".equals(normalized)) {
            task.setStartedAt(LocalDateTime.now());
        }
        if ("COMPLETED".equals(normalized)) {
            task.setCompletedAt(LocalDateTime.now());
            task.getRoom().setStatus(RoomStatus.AVAILABLE);
            rooms.save(task.getRoom());
        } else if ("CANCELLED".equals(normalized)) {
            task.setCancelledAt(LocalDateTime.now());
            if (task.getRoom().getStatus() != RoomStatus.OCCUPIED && task.getPreviousRoomStatus() != null) {
                task.getRoom().setStatus(RoomStatus.valueOf(task.getPreviousRoomStatus()));
                rooms.save(task.getRoom());
            }
        } else {
            task.getRoom().setStatus(targetRoomStatus(task, normalized));
            rooms.save(task.getRoom());
        }
        return new HousekeepingTaskResponse(tasks.save(task));
    }

    @Transactional
    public HousekeepingTaskResponse updateChecklist(Long id, ChecklistStepRequest request) {
        HousekeepingTask task = find(id);
        if (!"IN_PROGRESS".equals(task.getStatus())) {
            throw new IllegalArgumentException("Checklist can only be updated while task is IN_PROGRESS");
        }
        int totalSteps = HousekeepingChecklist.steps(task.getTaskType()).size();
        int stepIndex = request.getStepIndex();
        int completed = task.getCompletedSteps();
        if (stepIndex >= totalSteps) {
            throw new IllegalArgumentException("Checklist step does not exist: " + stepIndex);
        }
        if (request.isChecked()) {
            if (stepIndex < completed) return new HousekeepingTaskResponse(task);
            if (stepIndex != completed) {
                throw new IllegalArgumentException("Checklist steps must be completed in order");
            }
            task.setCompletedSteps(completed + 1);
        } else {
            if (stepIndex >= completed) return new HousekeepingTaskResponse(task);
            task.setCompletedSteps(stepIndex);
        }
        return new HousekeepingTaskResponse(tasks.save(task));
    }

    public void delete(Long id) {
        HousekeepingTask task = find(id);
        if (!"COMPLETED".equals(task.getStatus()) && task.getPreviousRoomStatus() != null
                && task.getRoom().getStatus() != RoomStatus.OCCUPIED) {
            task.getRoom().setStatus(RoomStatus.valueOf(task.getPreviousRoomStatus()));
            rooms.save(task.getRoom());
        }
        tasks.delete(task);
    }

    private HousekeepingTask find(Long id) {
        return tasks.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    private RoomStatus targetRoomStatus(HousekeepingTask task, String taskStatus) {
        if (!"CLEANING".equals(task.getTaskType())) return RoomStatus.MAINTENANCE;
        return "IN_PROGRESS".equals(taskStatus) ? RoomStatus.CLEANING : RoomStatus.DIRTY;
    }

    private void validateTransition(String current, String target) {
        if (current.equals(target)) return;
        boolean allowed = ("PENDING".equals(current) && "IN_PROGRESS".equals(target))
                || ("IN_PROGRESS".equals(current) && "COMPLETED".equals(target))
                || (("PENDING".equals(current) || "IN_PROGRESS".equals(current))
                    && "CANCELLED".equals(target));
        if (!allowed) {
            throw new IllegalArgumentException("Invalid housekeeping task transition: " + current + " -> " + target);
        }
    }
}
