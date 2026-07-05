package com.hotel.booking.controller;

import com.hotel.booking.dto.ReservationRoomRequest;
import com.hotel.booking.dto.ReservationRoomResponse;
import com.hotel.booking.service.ReservationRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings/reservation-rooms")
@Tag(name = "Reservation Rooms", description = "Physical room assignment within a reservation")
public class ReservationRoomController {

    private final ReservationRoomService reservationRoomService;

    public ReservationRoomController(ReservationRoomService reservationRoomService) {
        this.reservationRoomService = reservationRoomService;
    }

    @GetMapping
    @Operation(summary = "List room assignments for a reservation")
    public ResponseEntity<List<ReservationRoomResponse>> getByReservation(@RequestParam Long reservationId) {
        return ResponseEntity.ok(reservationRoomService.getByReservationId(reservationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room assignment by id")
    public ResponseEntity<ReservationRoomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationRoomService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Assign a room to a reservation (validates room is AVAILABLE, then marks it OCCUPIED)")
    public ResponseEntity<ReservationRoomResponse> assignRoom(@Valid @RequestBody ReservationRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationRoomService.assignRoom(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Release a room assignment (marks the room DIRTY for housekeeping)")
    public ResponseEntity<Void> release(@PathVariable Long id) {
        reservationRoomService.release(id);
        return ResponseEntity.noContent().build();
    }
}
