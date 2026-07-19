package com.hotel.room.dto;

import com.hotel.room.entity.HotelServiceItem;
import com.hotel.room.entity.ServiceCategory;

import java.math.BigDecimal;

public class HotelServiceResponse {

    private Long id;
    private String serviceName;
    private BigDecimal unitPrice;
    private ServiceCategory category;
    private String description;

    public HotelServiceResponse(HotelServiceItem item) {
        this.id = item.getId();
        this.serviceName = item.getServiceName();
        this.unitPrice = item.getUnitPrice();
        this.category = item.getCategory();
        this.description = item.getDescription();
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

    public ServiceCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}
