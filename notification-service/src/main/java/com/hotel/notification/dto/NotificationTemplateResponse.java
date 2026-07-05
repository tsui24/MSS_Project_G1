package com.hotel.notification.dto;

import com.hotel.notification.entity.NotificationTemplate;
import com.hotel.notification.entity.NotificationType;

public class NotificationTemplateResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String bodyTemplate;

    public NotificationTemplateResponse(NotificationTemplate template) {
        this.id = template.getId();
        this.type = template.getType();
        this.title = template.getTitle();
        this.bodyTemplate = template.getBodyTemplate();
    }

    public Long getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }
}
