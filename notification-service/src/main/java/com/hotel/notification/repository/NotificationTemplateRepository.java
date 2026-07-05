package com.hotel.notification.repository;

import com.hotel.notification.entity.NotificationTemplate;
import com.hotel.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByType(NotificationType type);
    boolean existsByType(NotificationType type);
}
