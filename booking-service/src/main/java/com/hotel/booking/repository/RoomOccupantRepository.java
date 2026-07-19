package com.hotel.booking.repository;

import com.hotel.booking.entity.RoomOccupant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomOccupantRepository extends JpaRepository<RoomOccupant, Long> {
    @Query("select occupant from RoomOccupant occupant " +
            "join fetch occupant.reservationRoom reservationRoom " +
            "where reservationRoom.id = :reservationRoomId order by occupant.id")
    List<RoomOccupant> findByReservationRoomId(@Param("reservationRoomId") Long reservationRoomId);
}
