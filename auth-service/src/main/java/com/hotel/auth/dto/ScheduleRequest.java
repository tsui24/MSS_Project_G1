package com.hotel.auth.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ScheduleRequest {
    @NotNull private Long staffId;
    @NotNull private Long shiftId;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
