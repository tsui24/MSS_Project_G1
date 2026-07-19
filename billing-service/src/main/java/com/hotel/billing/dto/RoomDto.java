package com.hotel.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDto {
    private Long id;
    private String roomNumber;
    private RoomClassDto roomClass;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoomClassDto {
        private String className;
        private BigDecimal basePrice;
        public String getClassName(){return className;} public void setClassName(String value){className=value;}
        public BigDecimal getBasePrice(){return basePrice;} public void setBasePrice(BigDecimal value){basePrice=value;}
    }
    public Long getId(){return id;} public void setId(Long value){id=value;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String value){roomNumber=value;}
    public RoomClassDto getRoomClass(){return roomClass;} public void setRoomClass(RoomClassDto value){roomClass=value;}
}
