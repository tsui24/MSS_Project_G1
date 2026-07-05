package com.hotel.room.dto;

import com.hotel.room.entity.HotelServiceItem;

import java.math.BigDecimal;

public class HotelServiceResponse {

    private Long id;
    private String serviceName;
    private BigDecimal unitPrice;

    public HotelServiceResponse(HotelServiceItem item) {
        this.id = item.getId();
        this.serviceName = item.getServiceName();
        this.unitPrice = item.getUnitPrice();
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
