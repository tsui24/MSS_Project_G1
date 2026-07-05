package com.hotel.billing.service;

import com.hotel.billing.dto.RevenueReportResponse;
import com.hotel.billing.exception.InvalidRangeException;
import com.hotel.billing.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ReportService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public ReportService(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public RevenueReportResponse getRevenueReport(LocalDate from, LocalDate to) {
        if (!to.isAfter(from)) {
            throw new InvalidRangeException("'to' date must be after 'from' date");
        }
        var fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        var toInstant = to.atStartOfDay(ZoneOffset.UTC).toInstant();
        var totalRevenue = paymentTransactionRepository.sumNetRevenue(fromInstant, toInstant);
        return new RevenueReportResponse(from, to, totalRevenue);
    }
}
