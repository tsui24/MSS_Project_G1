package com.hotel.auth.dto;

import com.hotel.auth.entity.WorkSchedule;
import java.time.LocalDate;

public class ScheduleResponse {
    private final Long id;
    private final Long staffId;
    private final Long shiftId;
    private final String shiftName;
    private final LocalDate workDate;
    private final String status;
    public ScheduleResponse(WorkSchedule s) {
        id = s.getId(); staffId = s.getStaff().getId(); shiftId = s.getShift().getId();
        shiftName = s.getShift().getName(); workDate = s.getWorkDate(); status = s.getStatus();
    }
    public Long getId() { return id; }
    public Long getStaffId() { return staffId; }
    public Long getShiftId() { return shiftId; }
    public String getShiftName() { return shiftName; }
    public LocalDate getWorkDate() { return workDate; }
    public String getStatus() { return status; }
}
