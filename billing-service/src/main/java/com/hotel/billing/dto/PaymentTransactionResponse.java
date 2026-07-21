package com.hotel.billing.dto;

import com.hotel.billing.entity.PaymentMethod;
import com.hotel.billing.entity.PaymentTransaction;
import com.hotel.billing.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentTransactionResponse {

    private Long id;
    private Long folioId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private TransactionType transactionType;
    private Instant createdAt;

    public PaymentTransactionResponse(PaymentTransaction transaction) {
        this.id = transaction.getId();
        this.folioId = transaction.getFolio().getId();
        this.amount = transaction.getAmount();
        this.paymentMethod = transaction.getPaymentMethod();
        this.transactionType = transaction.getTransactionType();
        this.createdAt = transaction.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getFolioId() {
        return folioId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCode() { return id == null ? null : "PAY-" + id; }
    public TransactionType getType() { return transactionType; }
    public String getStatus() { return "SUCCESS"; }
}
