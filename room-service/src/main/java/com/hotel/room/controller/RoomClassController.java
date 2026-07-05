package com.hotel.room.controller;

import com.hotel.room.dto.RoomClassRequest;
import com.hotel.room.dto.RoomClassResponse;
import com.hotel.room.service.RoomClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/room-classes")
@Tag(name = "Room Classes", description = "Room class catalog (Standard, Deluxe, Suite...)")
public class RoomClassController {

    private final RoomClassService roomClassService;

    public RoomClassController(RoomClassService roomClassService) {
        this.roomClassService = roomClassService;
    }

    @GetMapping
    @Operation(summary = "List all room classes")
    public ResponseEntity<List<RoomClassResponse>> getAll() {
        return ResponseEntity.ok(roomClassService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room class by id")
    public ResponseEntity<RoomClassResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomClassService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a room class")
    public ResponseEntity<RoomClassResponse> create(@Valid @RequestBody RoomClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomClassService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a room class")
    public ResponseEntity<RoomClassResponse> update(@PathVariable Long id, @Valid @RequestBody RoomClassRequest request) {
        return ResponseEntity.ok(roomClassService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room class")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomClassService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
