package com.hotel.room.dto;

import com.hotel.room.entity.DamageReport;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DamageReportResponse {
    private final Long id, staffId, roomId, reservationId;
    private final String roomNumber, itemName, description, status;
    private final Integer quantity;
    private final BigDecimal penaltyAmount;
    private final LocalDateTime createdAt;
    public DamageReportResponse(DamageReport r) {
        id=r.getId(); staffId=r.getStaffId(); roomId=r.getRoom().getId(); roomNumber=r.getRoom().getRoomNumber();
        reservationId=r.getReservationId(); itemName=r.getItemName(); description=r.getDescription();
        quantity=r.getQuantity(); penaltyAmount=r.getPenaltyAmount(); status=r.getStatus(); createdAt=r.getCreatedAt();
    }
    public Long getId(){return id;} public Long getStaffId(){return staffId;} public Long getRoomId(){return roomId;}
    public String getRoomNumber(){return roomNumber;} public Long getReservationId(){return reservationId;}
    public String getItemName(){return itemName;} public String getDescription(){return description;}
    public Integer getQuantity(){return quantity;} public BigDecimal getPenaltyAmount(){return penaltyAmount;}
    public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
}
