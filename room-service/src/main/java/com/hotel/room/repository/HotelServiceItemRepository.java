package com.hotel.room.repository;

import com.hotel.room.entity.HotelServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelServiceItemRepository extends JpaRepository<HotelServiceItem, Long> {
    boolean existsByServiceName(String serviceName);
}
