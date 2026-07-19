package com.hotel.room.repository;

import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    Page<Room> findByRoomClass_Id(Long roomClassId, Pageable pageable);

    Page<Room> findByStatusAndRoomClass_Id(RoomStatus status, Long roomClassId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select room from Room room where room.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}
