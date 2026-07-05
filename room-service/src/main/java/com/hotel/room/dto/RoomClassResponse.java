package com.hotel.room.dto;

import com.hotel.room.entity.RoomClass;

import java.math.BigDecimal;

public class RoomClassResponse {

    private Long id;
    private String className;
    private BigDecimal basePrice;

    public RoomClassResponse(RoomClass roomClass) {
        this.id = roomClass.getId();
        this.className = roomClass.getClassName();
        this.basePrice = roomClass.getBasePrice();
    }

    public Long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
