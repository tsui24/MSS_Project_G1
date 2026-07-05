package com.hotel.room.repository;

import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    Page<Room> findByRoomClass_Id(Long roomClassId, Pageable pageable);

    Page<Room> findByStatusAndRoomClass_Id(RoomStatus status, Long roomClassId, Pageable pageable);
}
