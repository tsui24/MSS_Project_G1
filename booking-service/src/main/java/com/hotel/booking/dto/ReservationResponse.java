package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private String bookingCode;
    private Long customerId;
    private BookingStatus bookingStatus;
    private AuthUserDto customer;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private List<ReservationRoomResponse> roomAssignments;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.bookingCode = reservation.getBookingCode();
        this.customerId = reservation.getCustomerId();
        this.bookingStatus = reservation.getBookingStatus();
        this.checkedInAt = reservation.getCheckedInAt();
        this.checkedOutAt = reservation.getCheckedOutAt();
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
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public LocalDateTime getCheckedOutAt() { return checkedOutAt; }
    public List<ReservationRoomResponse> getRoomAssignments() { return roomAssignments; }

    public void setCustomer(AuthUserDto customer) {
        this.customer = customer;
    }

    public void setRoomAssignments(List<ReservationRoom> assignments) {
        this.numberOfRooms = assignments.size();
        this.checkInDate = assignments.stream().map(ReservationRoom::getCheckInDate).min(LocalDate::compareTo).orElse(null);
        this.checkOutDate = assignments.stream().map(ReservationRoom::getCheckOutDate).max(LocalDate::compareTo).orElse(null);
        this.roomAssignments = assignments.stream().map(ReservationRoomResponse::new).toList();
    }
}
