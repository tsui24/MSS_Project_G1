package com.hotel.notification.controller;

import com.hotel.notification.dto.NotificationTemplateRequest;
import com.hotel.notification.dto.NotificationTemplateResponse;
import com.hotel.notification.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/templates")
@Tag(name = "Notification Templates", description = "Default message content per notification type")
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    public NotificationTemplateController(NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    @GetMapping
    @Operation(summary = "List all notification templates")
    public ResponseEntity<List<NotificationTemplateResponse>> getAll() {
        return ResponseEntity.ok(notificationTemplateService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create a template for a notification type")
    public ResponseEntity<NotificationTemplateResponse> create(@Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationTemplateService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a template")
    public ResponseEntity<NotificationTemplateResponse> update(@PathVariable Long id,
                                                                 @Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.ok(notificationTemplateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a template")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
