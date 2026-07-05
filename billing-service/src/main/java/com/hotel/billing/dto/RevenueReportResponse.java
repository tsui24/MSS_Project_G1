package com.hotel.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenueReportResponse {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalRevenue;

    public RevenueReportResponse(LocalDate from, LocalDate to, BigDecimal totalRevenue) {
        this.from = from;
        this.to = to;
        this.totalRevenue = totalRevenue;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}
