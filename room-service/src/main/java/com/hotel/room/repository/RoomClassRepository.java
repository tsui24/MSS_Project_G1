package com.hotel.room.repository;

import com.hotel.room.entity.RoomClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomClassRepository extends JpaRepository<RoomClass, Long> {
    boolean existsByClassName(String className);
}
