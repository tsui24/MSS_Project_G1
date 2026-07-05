package com.hotel.billing.dto;

import jakarta.validation.constraints.NotNull;

public class FolioRequest {

    @NotNull
    private Long reservationId;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}
