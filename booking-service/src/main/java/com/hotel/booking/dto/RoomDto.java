package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Mirrors the subset of room-service's RoomResponse fields booking-service needs to validate/assign
 * a room. room-service also nests a "roomClass" object in its response, which is ignored here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDto {

    private Long id;
    private String roomNumber;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
