package com.hotel.auth.service;

import com.hotel.auth.dto.ScheduleRequest;
import com.hotel.auth.dto.ScheduleResponse;
import com.hotel.auth.entity.Shift;
import com.hotel.auth.entity.User;
import com.hotel.auth.entity.WorkSchedule;
import com.hotel.auth.exception.ResourceNotFoundException;
import com.hotel.auth.repository.ShiftRepository;
import com.hotel.auth.repository.UserRepository;
import com.hotel.auth.repository.WorkScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {
    private final WorkScheduleRepository schedules;
    private final ShiftRepository shifts;
    private final UserRepository users;

    public ScheduleService(WorkScheduleRepository schedules, ShiftRepository shifts, UserRepository users) {
        this.schedules = schedules; this.shifts = shifts; this.users = users;
    }

    public List<ScheduleResponse> search(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("endDate must not be before startDate");
        return schedules.findByWorkDateBetweenOrderByWorkDateAsc(startDate, endDate).stream().map(ScheduleResponse::new).toList();
    }

    @Transactional
    public List<ScheduleResponse> create(ScheduleRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        User staff = users.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + request.getStaffId()));
        if (staff.getRole() == null || "CUSTOMER".equals(staff.getRole().getRoleName())) {
            throw new IllegalArgumentException("Selected user is not a staff account");
        }
        Shift shift = shifts.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + request.getShiftId()));
        List<ScheduleResponse> result = new ArrayList<>();
        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            if (!schedules.existsByStaffIdAndShiftIdAndWorkDate(staff.getId(), shift.getId(), date)) {
                WorkSchedule schedule = new WorkSchedule();
                schedule.setStaff(staff); schedule.setShift(shift); schedule.setWorkDate(date);
                result.add(new ScheduleResponse(schedules.save(schedule)));
            }
        }
        return result;
    }

    public void delete(Long id) {
        WorkSchedule schedule = schedules.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));
        schedules.delete(schedule);
    }
}
