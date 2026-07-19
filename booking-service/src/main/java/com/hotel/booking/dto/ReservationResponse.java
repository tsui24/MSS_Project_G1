package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;

import java.time.LocalDate;
import java.util.List;

public class ReservationResponse {

    private Long id;
    private String bookingCode;
    private Long customerId;
    private BookingStatus bookingStatus;
    private AuthUserDto customer;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;

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

    public AuthUserDto getCustomer() {
        return customer;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setCustomer(AuthUserDto customer) {
        this.customer = customer;
    }

    public void setRoomAssignments(List<ReservationRoom> assignments) {
        this.numberOfRooms = assignments.size();
        this.checkInDate = assignments.stream().map(ReservationRoom::getCheckInDate).min(LocalDate::compareTo).orElse(null);
        this.checkOutDate = assignments.stream().map(ReservationRoom::getCheckOutDate).max(LocalDate::compareTo).orElse(null);
    }
}
