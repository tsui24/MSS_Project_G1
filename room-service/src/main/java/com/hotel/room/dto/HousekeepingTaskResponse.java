package com.hotel.room.dto;

import com.hotel.room.entity.HousekeepingTask;
import java.time.LocalDateTime;

public class HousekeepingTaskResponse {
    private final Long id;
    private final Long roomId;
    private final String roomNumber;
    private final Long staffId;
    private final String assigneeName;
    private final String taskType;
    private final String status;
    private final LocalDateTime createdAt;
    public HousekeepingTaskResponse(HousekeepingTask task) {
        id = task.getId(); roomId = task.getRoom().getId(); roomNumber = task.getRoom().getRoomNumber();
        staffId = task.getStaffId(); assigneeName = task.getAssigneeName(); taskType = task.getTaskType();
        status = task.getStatus(); createdAt = task.getCreatedAt();
    }
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public Long getStaffId() { return staffId; }
    public String getAssigneeName() { return assigneeName; }
    public String getTaskType() { return taskType; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
