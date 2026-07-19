package com.hotel.auth.controller;

import com.hotel.auth.dto.ScheduleRequest;
import com.hotel.auth.dto.ScheduleResponse;
import com.hotel.auth.entity.Shift;
import com.hotel.auth.repository.ShiftRepository;
import com.hotel.auth.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ShiftRepository shiftRepository;
    public ScheduleController(ScheduleService scheduleService, ShiftRepository shiftRepository) {
        this.scheduleService = scheduleService; this.shiftRepository = shiftRepository;
    }

    @GetMapping("/shifts")
    public List<Shift> shifts() { return shiftRepository.findAll(); }

    @GetMapping("/schedules")
    public List<ScheduleResponse> schedules(
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return scheduleService.search(startDate, endDate);
    }

    @PostMapping("/schedules")
    public ResponseEntity<List<ScheduleResponse>> create(@Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
