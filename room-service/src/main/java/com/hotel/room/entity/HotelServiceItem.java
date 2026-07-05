package com.hotel.room.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Named HotelServiceItem (not HotelService) to avoid clashing with java.util.ServiceLoader-style
 * naming conventions and Spring's own "*Service" component suffix used across this codebase.
 */
@Entity
@Table(name = "hotel_services")
public class HotelServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false, unique = true, length = 150)
    private String serviceName;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
