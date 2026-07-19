package com.hotel.room.controller;

import com.hotel.room.dto.RoomRequest;
import com.hotel.room.dto.RoomResponse;
import com.hotel.room.dto.RoomStatusUpdateRequest;
import com.hotel.room.entity.RoomStatus;
import com.hotel.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/rooms")
@Tag(name = "Rooms", description = "Physical room inventory and status")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "List rooms, paginated and optionally filtered by status/room class")
    public ResponseEntity<Page<RoomResponse>> search(@RequestParam(name = "status", required = false) RoomStatus status,
                                                      @RequestParam(name = "roomClassId", required = false) Long roomClassId,
                                                      @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(roomService.search(status, roomClassId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by id")
    public ResponseEntity<RoomResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a room")
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a room")
    public ResponseEntity<RoomResponse> update(@PathVariable("id") Long id,
                                                @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update only a room's status (used by booking/housekeeping flows)")
    public ResponseEntity<RoomResponse> updateStatus(@PathVariable("id") Long id,
                                                      @Valid @RequestBody RoomStatusUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/status/compensate-check-in")
    @Operation(summary = "Compensate a failed distributed check-in by returning OCCUPIED to AVAILABLE")
    public ResponseEntity<RoomResponse> compensateFailedCheckIn(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.compensateFailedCheckIn(id));
    }

    @PatchMapping("/{id}/status/occupy-if-available")
    @Operation(summary = "Atomically occupy an AVAILABLE room for check-in")
    public ResponseEntity<RoomResponse> occupyIfAvailable(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.occupyIfAvailable(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
