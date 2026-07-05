package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationTemplateRequest;
import com.hotel.notification.dto.NotificationTemplateResponse;
import com.hotel.notification.entity.NotificationTemplate;
import com.hotel.notification.exception.DuplicateResourceException;
import com.hotel.notification.exception.ResourceNotFoundException;
import com.hotel.notification.repository.NotificationTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationTemplateService(NotificationTemplateRepository notificationTemplateRepository) {
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public List<NotificationTemplateResponse> getAll() {
        return notificationTemplateRepository.findAll().stream().map(NotificationTemplateResponse::new).toList();
    }

    public NotificationTemplateResponse create(NotificationTemplateRequest request) {
        if (notificationTemplateRepository.existsByType(request.getType())) {
            throw new DuplicateResourceException("A template already exists for type: " + request.getType());
        }
        NotificationTemplate template = new NotificationTemplate();
        template.setType(request.getType());
        template.setTitle(request.getTitle());
        template.setBodyTemplate(request.getBodyTemplate());
        return new NotificationTemplateResponse(notificationTemplateRepository.save(template));
    }

    public NotificationTemplateResponse update(Long id, NotificationTemplateRequest request) {
        NotificationTemplate template = findEntity(id);
        template.setType(request.getType());
        template.setTitle(request.getTitle());
        template.setBodyTemplate(request.getBodyTemplate());
        return new NotificationTemplateResponse(notificationTemplateRepository.save(template));
    }

    public void delete(Long id) {
        notificationTemplateRepository.delete(findEntity(id));
    }

    private NotificationTemplate findEntity(Long id) {
        return notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));
    }
}
