package com.hotel.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DamageReportDto {
    private Long id;
    private String roomNumber;
    private String itemName;
    private String description;
    private BigDecimal penaltyAmount;
    private String status;
    public Long getId(){return id;} public void setId(Long value){id=value;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String value){roomNumber=value;}
    public String getItemName(){return itemName;} public void setItemName(String value){itemName=value;}
    public String getDescription(){return description;} public void setDescription(String value){description=value;}
    public BigDecimal getPenaltyAmount(){return penaltyAmount;} public void setPenaltyAmount(BigDecimal value){penaltyAmount=value;}
    public String getStatus(){return status;} public void setStatus(String value){status=value;}
}
