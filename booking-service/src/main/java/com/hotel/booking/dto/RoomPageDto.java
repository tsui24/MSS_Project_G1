package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Only binds the "content" field of room-service's Spring Data {@code Page<RoomResponse>} JSON;
 * the pagination metadata (totalElements, totalPages...) isn't needed here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomPageDto {

    private List<RoomDto> content;

    public List<RoomDto> getContent() {
        return content;
    }

    public void setContent(List<RoomDto> content) {
        this.content = content;
    }
}
