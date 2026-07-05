package com.hotel.notification.dto;

import com.hotel.notification.entity.Notification;
import com.hotel.notification.entity.NotificationStatus;
import com.hotel.notification.entity.NotificationType;

import java.time.Instant;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long reservationId;
    private NotificationType type;
    private String message;
    private NotificationStatus status;
    private Instant createdAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUserId();
        this.reservationId = notification.getReservationId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.status = notification.getStatus();
        this.createdAt = notification.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
