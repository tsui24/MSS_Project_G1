package com.hotel.room.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DamageReportRequest {
    @NotNull private Long staffId;
    @NotNull private Long roomId;
    private Long reservationId;
    @NotBlank private String itemName;
    @NotBlank private String description;
    @NotNull @Min(1) private Integer quantity;
    @NotNull @DecimalMin("0.01") private BigDecimal penaltyAmount;
    public Long getStaffId() { return staffId; } public void setStaffId(Long value) { staffId = value; }
    public Long getRoomId() { return roomId; } public void setRoomId(Long value) { roomId = value; }
    public Long getReservationId() { return reservationId; } public void setReservationId(Long value) { reservationId = value; }
    public String getItemName() { return itemName; } public void setItemName(String value) { itemName = value; }
    public String getDescription() { return description; } public void setDescription(String value) { description = value; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer value) { quantity = value; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; } public void setPenaltyAmount(BigDecimal value) { penaltyAmount = value; }
}
