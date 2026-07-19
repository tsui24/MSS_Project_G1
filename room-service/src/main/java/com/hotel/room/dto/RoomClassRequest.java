package com.hotel.room.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;

public class RoomClassRequest {

    @NotBlank
    private String className;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal basePrice;

    @NotNull
    @Min(1)
    private Integer standardOccupancy;

    @NotNull
    @Min(1)
    private Integer maxOccupancy;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal extraPersonFee;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getStandardOccupancy() {
        return standardOccupancy;
    }

    public void setStandardOccupancy(Integer standardOccupancy) {
        this.standardOccupancy = standardOccupancy;
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    @AssertTrue(message = "maxOccupancy must be greater than or equal to standardOccupancy")
    public boolean isOccupancyRangeValid() {
        return standardOccupancy == null || maxOccupancy == null || maxOccupancy >= standardOccupancy;
    }

    public BigDecimal getExtraPersonFee() {
        return extraPersonFee;
    }

    public void setExtraPersonFee(BigDecimal extraPersonFee) {
        this.extraPersonFee = extraPersonFee;
    }
}
