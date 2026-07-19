package com.hotel.room.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "damage_reports")
public class DamageReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "staff_id", nullable = false) private Long staffId;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "room_id", nullable = false) private Room room;
    @Column(name = "reservation_id") private Long reservationId;
    @Column(name = "item_name", nullable = false, length = 120) private String itemName;
    @Column(nullable = false, length = 500) private String description;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2) private BigDecimal penaltyAmount;
    @Column(nullable = false, length = 20) private String status = "OPEN";
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
