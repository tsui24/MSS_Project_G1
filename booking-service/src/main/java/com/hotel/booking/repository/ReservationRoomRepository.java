package com.hotel.booking.repository;

import com.hotel.booking.entity.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
    List<ReservationRoom> findByReservationId(Long reservationId);

    /**
     * Rooms already booked (any non-cancelled reservation) whose stay overlaps the requested
     * [checkIn, checkOut) range, restricted to the given candidate room ids.
     */
    @Query("SELECT rr FROM ReservationRoom rr WHERE rr.roomId IN :roomIds " +
            "AND rr.reservation.bookingStatus <> 'CANCELLED' " +
            "AND rr.checkInDate < :checkOutDate AND rr.checkOutDate > :checkInDate")
    List<ReservationRoom> findOverlapping(@Param("roomIds") List<Long> roomIds,
                                           @Param("checkInDate") LocalDate checkInDate,
                                           @Param("checkOutDate") LocalDate checkOutDate);
}
