package com.hotel.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CheckInRequest {
    @NotEmpty private List<@Valid CheckInRoomRequest> roomAssignments;

    public List<CheckInRoomRequest> getRoomAssignments() { return roomAssignments; }
    public void setRoomAssignments(List<CheckInRoomRequest> roomAssignments) {
        this.roomAssignments = roomAssignments;
    }
}
