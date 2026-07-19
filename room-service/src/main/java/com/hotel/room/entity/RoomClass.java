package com.hotel.room.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_classes")
public class RoomClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, unique = true, length = 100)
    private String className;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "standard_occupancy", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 1")
    private Integer standardOccupancy = 1;

    @Column(name = "max_occupancy", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 2")
    private Integer maxOccupancy = 2;

    @Column(name = "extra_person_fee", nullable = false, precision = 12, scale = 2,
            columnDefinition = "DECIMAL(12,2) NOT NULL DEFAULT 0.00")
    private BigDecimal extraPersonFee = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getExtraPersonFee() {
        return extraPersonFee;
    }

    public void setExtraPersonFee(BigDecimal extraPersonFee) {
        this.extraPersonFee = extraPersonFee;
    }
}
