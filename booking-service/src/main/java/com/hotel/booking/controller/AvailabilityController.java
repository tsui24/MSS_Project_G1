package com.hotel.booking.controller;

import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings/availability")
@Tag(name = "Availability", description = "Search for rooms with no overlapping booking in a date range")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @Operation(summary = "Find AVAILABLE rooms (optionally of a given room class) free for the whole date range")
    public ResponseEntity<List<RoomDto>> search(
            @RequestParam(name = "roomClassId", required = false) Long roomClassId,
            @RequestParam(name = "checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(name = "checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        return ResponseEntity.ok(availabilityService.findAvailableRooms(roomClassId, checkInDate, checkOutDate));
    }
}
