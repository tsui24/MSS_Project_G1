package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;
import com.hotel.notification.entity.Notification;
import com.hotel.notification.entity.NotificationStatus;
import com.hotel.notification.entity.NotificationTemplate;
import com.hotel.notification.entity.NotificationType;
import com.hotel.notification.exception.ResourceNotFoundException;
import com.hotel.notification.repository.NotificationRepository;
import com.hotel.notification.repository.NotificationTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationService(NotificationRepository notificationRepository,
                                NotificationTemplateRepository notificationTemplateRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public Page<NotificationResponse> search(Long userId, Pageable pageable) {
        Page<Notification> page = userId != null
                ? notificationRepository.findByUserId(userId, pageable)
                : notificationRepository.findAll(pageable);
        return page.map(NotificationResponse::new);
    }

    public NotificationResponse getById(Long id) {
        return new NotificationResponse(findEntity(id));
    }

    /**
     * No real email/SMS provider is wired up for this project, so "sending" just persists the
     * notification and immediately marks it SENT to simulate a successful delivery.
     */
    public NotificationResponse create(NotificationRequest request) {
        String message = request.getMessage() != null && !request.getMessage().isBlank()
                ? request.getMessage()
                : resolveFromTemplate(request.getType());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setReservationId(request.getReservationId());
        notification.setType(request.getType());
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.SENT);
        return new NotificationResponse(notificationRepository.save(notification));
    }

    private String resolveFromTemplate(NotificationType type) {
        NotificationTemplate template = notificationTemplateRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No message provided and no template registered for type: " + type));
        return template.getBodyTemplate();
    }

    public void delete(Long id) {
        notificationRepository.delete(findEntity(id));
    }

    private Notification findEntity(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }
}
