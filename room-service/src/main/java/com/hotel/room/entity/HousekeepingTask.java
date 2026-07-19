package com.hotel.room.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_tasks")
public class HousekeepingTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    @Column(name = "staff_id", nullable = false)
    private Long staffId;
    @Column(name = "assignee_name", length = 150)
    private String assigneeName;
    @Column(name = "task_type", nullable = false, length = 40)
    private String taskType;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(name = "previous_room_status", length = 20)
    private String previousRoomStatus;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPreviousRoomStatus() { return previousRoomStatus; }
    public void setPreviousRoomStatus(String previousRoomStatus) { this.previousRoomStatus = previousRoomStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
