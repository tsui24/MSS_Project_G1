package com.hotel.room.dto;

import com.hotel.room.entity.HousekeepingTask;
import java.time.LocalDateTime;
import java.util.List;
import com.hotel.room.service.HousekeepingChecklist;

public class HousekeepingTaskResponse {
    private final Long id;
    private final Long roomId;
    private final String roomNumber;
    private final Long staffId;
    private final String assigneeName;
    private final String taskType;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime cancelledAt;
    private final Integer completedSteps;
    private final List<String> checklist;
    public HousekeepingTaskResponse(HousekeepingTask task) {
        id = task.getId(); roomId = task.getRoom().getId(); roomNumber = task.getRoom().getRoomNumber();
        staffId = task.getStaffId(); assigneeName = task.getAssigneeName(); taskType = task.getTaskType();
        status = task.getStatus(); createdAt = task.getCreatedAt();
        startedAt = task.getStartedAt(); completedAt = task.getCompletedAt(); cancelledAt = task.getCancelledAt();
        completedSteps = task.getCompletedSteps(); checklist = HousekeepingChecklist.steps(task.getTaskType());
    }
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public Long getStaffId() { return staffId; }
    public String getAssigneeName() { return assigneeName; }
    public String getTaskType() { return taskType; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public Integer getCompletedSteps() { return completedSteps; }
    public List<String> getChecklist() { return checklist; }
}
