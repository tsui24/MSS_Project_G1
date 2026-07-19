package com.hotel.booking.dto;

import com.hotel.booking.entity.ReservationRoom;

import java.time.LocalDate;

public class ReservationRoomResponse {

    private Long id;
    private Long reservationId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;

    public ReservationRoomResponse(ReservationRoom reservationRoom) {
        this.id = reservationRoom.getId();
        this.reservationId = reservationRoom.getReservation().getId();
        this.roomId = reservationRoom.getRoomId();
        this.checkInDate = reservationRoom.getCheckInDate();
        this.checkOutDate = reservationRoom.getCheckOutDate();
        this.guestCount = reservationRoom.getGuestCount();
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public Integer getGuestCount() { return guestCount; }
}
