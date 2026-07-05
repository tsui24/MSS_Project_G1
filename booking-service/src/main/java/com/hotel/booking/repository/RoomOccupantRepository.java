package com.hotel.booking.repository;

import com.hotel.booking.entity.RoomOccupant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomOccupantRepository extends JpaRepository<RoomOccupant, Long> {
    List<RoomOccupant> findByReservationRoomId(Long reservationRoomId);
}
