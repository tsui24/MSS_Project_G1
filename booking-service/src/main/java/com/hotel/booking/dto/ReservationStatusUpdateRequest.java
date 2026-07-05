package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class ReservationStatusUpdateRequest {

    @NotNull
    private BookingStatus bookingStatus;

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
