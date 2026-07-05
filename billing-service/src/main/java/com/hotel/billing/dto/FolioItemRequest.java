package com.hotel.billing.dto;

import com.hotel.billing.entity.FolioItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FolioItemRequest {

    @NotNull
    private Long folioId;

    @NotNull
    private FolioItemType itemType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    public Long getFolioId() {
        return folioId;
    }

    public void setFolioId(Long folioId) {
        this.folioId = folioId;
    }

    public FolioItemType getItemType() {
        return itemType;
    }

    public void setItemType(FolioItemType itemType) {
        this.itemType = itemType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
