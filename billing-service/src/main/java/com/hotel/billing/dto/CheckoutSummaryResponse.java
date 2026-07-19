package com.hotel.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CheckoutSummaryResponse {
    private final Long reservationId;
    private final String bookingCode;
    private final String bookingStatus;
    private final Long customerId;
    private final Long folioId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final long nights;
    private final BigDecimal totalCharges;
    private final BigDecimal totalPaid;
    private final BigDecimal balance;
    private final boolean canCheckout;
    private final List<FolioItemResponse> items;
    private final List<PaymentTransactionResponse> payments;

    public CheckoutSummaryResponse(ReservationDto reservation, Long folioId, long nights,
            BigDecimal totalCharges, BigDecimal totalPaid, BigDecimal balance,
            List<FolioItemResponse> items, List<PaymentTransactionResponse> payments) {
        this.reservationId=reservation.getId(); this.bookingCode=reservation.getBookingCode();
        this.bookingStatus=reservation.getBookingStatus(); this.customerId=reservation.getCustomerId(); this.folioId=folioId;
        this.checkInDate=reservation.getCheckInDate(); this.checkOutDate=reservation.getCheckOutDate(); this.nights=nights;
        this.totalCharges=totalCharges; this.totalPaid=totalPaid; this.balance=balance;
        this.canCheckout=balance.compareTo(BigDecimal.ZERO)<=0; this.items=items; this.payments=payments;
    }
    public Long getReservationId(){return reservationId;} public String getBookingCode(){return bookingCode;}
    public String getBookingStatus(){return bookingStatus;} public Long getFolioId(){return folioId;}
    public Long getCustomerId(){return customerId;}
    public LocalDate getCheckInDate(){return checkInDate;} public LocalDate getCheckOutDate(){return checkOutDate;}
    public long getNights(){return nights;} public BigDecimal getTotalCharges(){return totalCharges;}
    public BigDecimal getTotalPaid(){return totalPaid;} public BigDecimal getBalance(){return balance;}
    public boolean isCanCheckout(){return canCheckout;} public List<FolioItemResponse> getItems(){return items;}
    public List<PaymentTransactionResponse> getPayments(){return payments;}
}
