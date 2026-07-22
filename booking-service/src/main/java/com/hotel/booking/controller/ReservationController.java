package com.hotel.booking.controller;

import com.hotel.booking.dto.ReservationRequest;
import com.hotel.booking.dto.ReservationResponse;
import com.hotel.booking.dto.ReservationStatusUpdateRequest;
import com.hotel.booking.dto.CheckInRequest;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings/reservations")
@Tag(name = "Reservations", description = "Reservation lifecycle (PENDING, IN_HOUSE, CHECKED_OUT, CANCELLED)")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "List reservations, paginated and optionally filtered by customer/status")
    public ResponseEntity<Page<ReservationResponse>> search(@RequestParam(name = "customerId", required = false) Long customerId,
                                                             @RequestParam(name = "status", required = false) BookingStatus status,
                                                             @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reservationService.search(customerId, status, pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "Dashboard counts of reservations grouped by booking status")
    public ResponseEntity<Map<BookingStatus, Long>> getStats() {
        return ResponseEntity.ok(reservationService.getStatusStats());
    }

    @PostMapping("/reconcile-room-statuses")
    @Operation(summary = "Repair room statuses owned by active IN_HOUSE reservations")
    public ResponseEntity<Map<String, Long>> reconcileRoomStatuses() {
        return ResponseEntity.ok(Map.of("updatedRooms", reservationService.reconcileInHouseRoomStatuses()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by id")
    public ResponseEntity<ReservationResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a reservation (validates the customer exists in auth-service)")
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Arbitrary booking status correction (prefer check-in/check-out/cancel for normal flow)")
    public ResponseEntity<ReservationResponse> updateStatus(@PathVariable("id") Long id,
                                                              @Valid @RequestBody ReservationStatusUpdateRequest request) {
        return ResponseEntity.ok(reservationService.updateStatus(id, request.getBookingStatus()));
    }

    @PatchMapping("/{id}/identity-card")
    @Operation(summary = "Update identity card on a PENDING reservation")
    public ResponseEntity<ReservationResponse> updateIdentityCard(@PathVariable("id") Long id,
                                                                    @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.updateIdentityCard(id, request.getIdentityCard()));
    }

    @PatchMapping("/{id}/check-in")
    @Operation(summary = "Atomically assign rooms, register all occupants, and check in a PENDING reservation")
    public ResponseEntity<ReservationResponse> checkIn(@PathVariable("id") Long id,
                                                        @Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(reservationService.checkIn(id, request));
    }

    @PatchMapping("/{id}/check-out")
    @Operation(summary = "Check out an IN_HOUSE reservation; releases its rooms to DIRTY for housekeeping")
    public ResponseEntity<ReservationResponse> checkOut(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.checkOut(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a PENDING reservation; releases any assigned rooms back to AVAILABLE")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.cancel(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
