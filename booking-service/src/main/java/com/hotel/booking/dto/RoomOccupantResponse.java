package com.hotel.booking.dto;

import com.hotel.booking.entity.RoomOccupant;

public class RoomOccupantResponse {

    private Long id;
    private Long reservationRoomId;
    private String guestName;

    public RoomOccupantResponse(RoomOccupant occupant) {
        this.id = occupant.getId();
        this.reservationRoomId = occupant.getReservationRoom().getId();
        this.guestName = occupant.getGuestName();
    }

    public Long getId() {
        return id;
    }

    public Long getReservationRoomId() {
        return reservationRoomId;
    }

    public String getGuestName() {
        return guestName;
    }
}
