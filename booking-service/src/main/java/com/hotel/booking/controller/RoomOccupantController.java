package com.hotel.booking.controller;

import com.hotel.booking.dto.RoomOccupantRequest;
import com.hotel.booking.dto.RoomOccupantResponse;
import com.hotel.booking.service.RoomOccupantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings/room-occupants")
@Tag(name = "Room Occupants", description = "Actual guests staying in an assigned room, for tam tru declarations")
public class RoomOccupantController {

    private final RoomOccupantService roomOccupantService;

    public RoomOccupantController(RoomOccupantService roomOccupantService) {
        this.roomOccupantService = roomOccupantService;
    }

    @GetMapping
    @Operation(summary = "List occupants for a room assignment")
    public ResponseEntity<List<RoomOccupantResponse>> getByReservationRoom(
            @RequestParam(name = "reservationRoomId") Long reservationRoomId) {
        return ResponseEntity.ok(roomOccupantService.getByReservationRoomId(reservationRoomId));
    }

    @PostMapping
    @Operation(summary = "Register a guest occupant in an assigned room")
    public ResponseEntity<RoomOccupantResponse> create(@Valid @RequestBody RoomOccupantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomOccupantService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a guest occupant")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        roomOccupantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
