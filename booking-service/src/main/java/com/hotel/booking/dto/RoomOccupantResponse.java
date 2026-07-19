package com.hotel.booking.dto;

import com.hotel.booking.entity.RoomOccupant;

public class RoomOccupantResponse {

    private Long id;
    private Long reservationRoomId;
    private String guestName;
    private String phoneNumber;
    private String identityDocument;
    private String residence;

    public RoomOccupantResponse(RoomOccupant occupant) {
        this.id = occupant.getId();
        this.reservationRoomId = occupant.getReservationRoom().getId();
        this.guestName = occupant.getGuestName();
        this.phoneNumber = occupant.getPhoneNumber();
        this.identityDocument = occupant.getIdentityDocument();
        this.residence = occupant.getResidence();
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

    public String getPhoneNumber() { return phoneNumber; }
    public String getIdentityDocument() { return identityDocument; }
    public String getResidence() { return residence; }
}
