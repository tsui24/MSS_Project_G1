package com.hotel.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckInRoomRequest {
    @NotNull private Long reservationRoomId;
    @NotNull private Long roomId;
    @NotEmpty private List<@Valid CheckInOccupantRequest> occupants;

    public Long getReservationRoomId() { return reservationRoomId; }
    public void setReservationRoomId(Long reservationRoomId) { this.reservationRoomId = reservationRoomId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public List<CheckInOccupantRequest> getOccupants() { return occupants; }
    public void setOccupants(List<CheckInOccupantRequest> occupants) { this.occupants = occupants; }
}
