package com.hotel.billing.dto;

import com.hotel.billing.entity.FolioItem;
import com.hotel.billing.entity.FolioItemType;

import java.math.BigDecimal;
import java.time.Instant;

public class FolioItemResponse {

    private Long id;
    private Long folioId;
    private FolioItemType itemType;
    private BigDecimal amount;
    private Instant createdAt;

    public FolioItemResponse(FolioItem item) {
        this.id = item.getId();
        this.folioId = item.getFolio().getId();
        this.itemType = item.getItemType();
        this.amount = item.getAmount();
        this.createdAt = item.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getFolioId() {
        return folioId;
    }

    public FolioItemType getItemType() {
        return itemType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
