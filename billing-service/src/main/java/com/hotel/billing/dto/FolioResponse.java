package com.hotel.billing.dto;

import com.hotel.billing.entity.Folio;

import java.math.BigDecimal;

public class FolioResponse {

    private Long id;
    private Long reservationId;
    private BigDecimal balance;

    public FolioResponse(Folio folio) {
        this.id = folio.getId();
        this.reservationId = folio.getReservationId();
        this.balance = folio.getBalance();
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
