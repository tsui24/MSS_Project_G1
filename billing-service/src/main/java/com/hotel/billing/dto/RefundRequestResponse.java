package com.hotel.billing.dto;

import com.hotel.billing.entity.RefundRequest;
import com.hotel.billing.entity.RefundStatus;
import java.math.BigDecimal;
import java.time.Instant;

public class RefundRequestResponse {
    private final Long id;
    private final BigDecimal amount;
    private final String reason;
    private final String requestedByName;
    private final RefundStatus status;
    private final String rejectReason;
    private final Instant createdAt;
    private final Instant resolvedAt;
    private final PaymentTransactionResponse paymentTransaction;

    public RefundRequestResponse(RefundRequest request) {
        id = request.getId();
        amount = request.getAmount();
        reason = request.getReason();
        requestedByName = request.getRequestedByName();
        status = request.getStatus();
        rejectReason = request.getRejectReason();
        createdAt = request.getCreatedAt();
        resolvedAt = request.getResolvedAt();
        paymentTransaction = new PaymentTransactionResponse(request.getPaymentTransaction());
    }
    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getReason() { return reason; }
    public String getRequestedByName() { return requestedByName; }
    public RefundStatus getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public PaymentTransactionResponse getPaymentTransaction() { return paymentTransaction; }
}
