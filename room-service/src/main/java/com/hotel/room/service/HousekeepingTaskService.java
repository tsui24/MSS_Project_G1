package com.hotel.room.service;

import com.hotel.room.dto.HousekeepingTaskRequest;
import com.hotel.room.dto.HousekeepingTaskResponse;
import com.hotel.room.entity.HousekeepingTask;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;
import com.hotel.room.exception.DuplicateResourceException;
import com.hotel.room.exception.ResourceNotFoundException;
import com.hotel.room.repository.HousekeepingTaskRepository;
import com.hotel.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Set;

@Service
public class HousekeepingTaskService {
    private static final Set<String> STATUSES = Set.of("PENDING", "IN_PROGRESS", "COMPLETED");
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
            RoomStatus target = "CLEANING".equals(task.getTaskType()) ? RoomStatus.DIRTY : RoomStatus.MAINTENANCE;
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
        HousekeepingTask task = new HousekeepingTask();
        task.setRoom(room); task.setStaffId(request.getStaffId());
        task.setAssigneeName(request.getAssigneeName()); task.setTaskType(request.getTaskType());
        task.setPreviousRoomStatus(room.getStatus().name());
        room.setStatus("CLEANING".equals(request.getTaskType()) ? RoomStatus.DIRTY : RoomStatus.MAINTENANCE);
        rooms.save(room);
        return new HousekeepingTaskResponse(tasks.save(task));
    }

    public HousekeepingTaskResponse updateStatus(Long id, String status) {
        String normalized = status.toUpperCase();
        if (!STATUSES.contains(normalized)) throw new IllegalArgumentException("Invalid task status: " + status);
        HousekeepingTask task = find(id);
        task.setStatus(normalized);
        if ("COMPLETED".equals(normalized)) {
            task.getRoom().setStatus(RoomStatus.AVAILABLE);
            rooms.save(task.getRoom());
        }
        return new HousekeepingTaskResponse(tasks.save(task));
    }

    public void delete(Long id) {
        HousekeepingTask task = find(id);
        if (!"COMPLETED".equals(task.getStatus()) && task.getPreviousRoomStatus() != null) {
            task.getRoom().setStatus(RoomStatus.valueOf(task.getPreviousRoomStatus()));
            rooms.save(task.getRoom());
        }
        tasks.delete(task);
    }

    private HousekeepingTask find(Long id) {
        return tasks.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }
}
