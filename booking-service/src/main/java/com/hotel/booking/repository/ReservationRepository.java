package com.hotel.booking.repository;

import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByBookingCode(String bookingCode);

    Page<Reservation> findByCustomerId(Long customerId, Pageable pageable);

    Page<Reservation> findByBookingStatus(BookingStatus bookingStatus, Pageable pageable);

    Page<Reservation> findByCustomerIdAndBookingStatus(Long customerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("SELECT r.bookingStatus, COUNT(r) FROM Reservation r GROUP BY r.bookingStatus")
    List<Object[]> countGroupedByStatus();
}
