package com.hotel.room.dto;

import com.hotel.room.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;

public class RoomStatusUpdateRequest {

    @NotNull
    private RoomStatus status;

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
