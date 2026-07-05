package com.hotel.room.dto;

import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;

public class RoomResponse {

    private Long id;
    private String roomNumber;
    private RoomClassResponse roomClass;
    private RoomStatus status;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.roomClass = new RoomClassResponse(room.getRoomClass());
        this.status = room.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomClassResponse getRoomClass() {
        return roomClass;
    }

    public RoomStatus getStatus() {
        return status;
    }
}
