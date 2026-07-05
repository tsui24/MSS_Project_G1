package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;

public class ReservationResponse {

    private Long id;
    private String bookingCode;
    private Long customerId;
    private BookingStatus bookingStatus;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.bookingCode = reservation.getBookingCode();
        this.customerId = reservation.getCustomerId();
        this.bookingStatus = reservation.getBookingStatus();
    }

    public Long getId() {
        return id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
}
