package com.hotel.room.dto;

import com.hotel.room.entity.RoomClass;

import java.math.BigDecimal;

public class RoomClassResponse {

    private Long id;
    private String className;
    private BigDecimal basePrice;
    private Integer standardOccupancy;
    private Integer maxOccupancy;
    private BigDecimal extraPersonFee;

    public RoomClassResponse(RoomClass roomClass) {
        this.id = roomClass.getId();
        this.className = roomClass.getClassName();
        this.basePrice = roomClass.getBasePrice();
        this.standardOccupancy = roomClass.getStandardOccupancy();
        this.maxOccupancy = roomClass.getMaxOccupancy();
        this.extraPersonFee = roomClass.getExtraPersonFee();
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

    public Integer getStandardOccupancy() {
        return standardOccupancy;
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public BigDecimal getExtraPersonFee() {
        return extraPersonFee;
    }
}
