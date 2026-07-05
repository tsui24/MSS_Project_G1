package com.hotel.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public class FolioStatementResponse {

    private Long folioId;
    private Long reservationId;
    private BigDecimal balance;
    private List<FolioItemResponse> items;
    private List<PaymentTransactionResponse> payments;

    public FolioStatementResponse(FolioResponse folio, List<FolioItemResponse> items,
                                   List<PaymentTransactionResponse> payments) {
        this.folioId = folio.getId();
        this.reservationId = folio.getReservationId();
        this.balance = folio.getBalance();
        this.items = items;
        this.payments = payments;
    }

    public Long getFolioId() {
        return folioId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<FolioItemResponse> getItems() {
        return items;
    }

    public List<PaymentTransactionResponse> getPayments() {
        return payments;
    }
}
