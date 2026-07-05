package com.hotel.notification.controller;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;
import com.hotel.notification.service.NotificationService;
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
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Guest notifications (booking confirmation, check-in/out reminders, invoice)")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "List notifications, paginated and optionally filtered by user")
    public ResponseEntity<Page<NotificationResponse>> search(@RequestParam(required = false) Long userId,
                                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.search(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a notification by id")
    public ResponseEntity<NotificationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Send a notification (simulated delivery, no real email/SMS provider)")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
