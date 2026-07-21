package com.hotel.room.dto;

import com.hotel.room.entity.RoomClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoomClassResponse {

    private Long id;
    private String className;
    private BigDecimal basePrice;
    private Integer standardOccupancy;
    private Integer maxOccupancy;
    private BigDecimal extraPersonFee;
    private List<String> amenities;

    public RoomClassResponse(RoomClass roomClass) {
        this.id = roomClass.getId();
        this.className = roomClass.getClassName();
        this.basePrice = roomClass.getBasePrice();
        this.standardOccupancy = roomClass.getStandardOccupancy();
        this.maxOccupancy = roomClass.getMaxOccupancy();
        this.extraPersonFee = roomClass.getExtraPersonFee();
        this.amenities = roomClass.getAmenities() != null
                ? new ArrayList<>(roomClass.getAmenities())
                : List.of();
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

    public List<String> getAmenities() {
        return amenities;
    }
}
