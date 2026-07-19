package com.hotel.billing.service;

import com.hotel.billing.client.BookingServiceClient;
import com.hotel.billing.client.RoomServiceClient;
import com.hotel.billing.dto.*;
import com.hotel.billing.entity.*;
import com.hotel.billing.exception.InvalidStateException;
import com.hotel.billing.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CheckoutService {
    private final BookingServiceClient bookings;
    private final RoomServiceClient rooms;
    private final FolioRepository folios;
    private final FolioItemRepository items;
    private final PaymentTransactionRepository payments;
    private final FolioService folioService;

    public CheckoutService(BookingServiceClient bookings, RoomServiceClient rooms, FolioRepository folios,
                           FolioItemRepository items, PaymentTransactionRepository payments, FolioService folioService) {
        this.bookings=bookings; this.rooms=rooms; this.folios=folios; this.items=items;
        this.payments=payments; this.folioService=folioService;
    }

    @Transactional
    public CheckoutSummaryResponse prepare(Long reservationId) {
        ReservationDto reservation=bookings.getReservation(reservationId);
        if (!List.of("IN_HOUSE","CHECKED_OUT").contains(reservation.getBookingStatus())) {
            throw new InvalidStateException("Checkout summary is only available for IN_HOUSE or CHECKED_OUT reservations");
        }
        Folio folio=folios.findByReservationId(reservationId).orElseGet(() -> {
            Folio created=new Folio(); created.setReservationId(reservationId); created.setBalance(BigDecimal.ZERO);
            return folios.save(created);
        });

        BigDecimal nightlyBase=BigDecimal.ZERO;
        List<ReservationDto.ReservationRoomDto> assignments = reservation.getRoomAssignments()==null
                ? List.of() : reservation.getRoomAssignments();
        for (ReservationDto.ReservationRoomDto assignment: assignments) {
            RoomDto room=rooms.getRoom(assignment.getRoomId());
            BigDecimal rate=room.getRoomClass()==null || room.getRoomClass().getBasePrice()==null
                    ? BigDecimal.ZERO : room.getRoomClass().getBasePrice();
            long roomNights=Math.max(1,ChronoUnit.DAYS.between(assignment.getCheckInDate(),assignment.getCheckOutDate()));
            BigDecimal amount=rate.multiply(BigDecimal.valueOf(roomNights));
            String className=room.getRoomClass()==null ? "Room" : room.getRoomClass().getClassName();
            syncCharge(folio,FolioItemType.ROOM_CHARGE,"ROOM:"+assignment.getId(),
                    "Room "+room.getRoomNumber()+" - "+className+" ("+roomNights+" night(s))",amount);
            nightlyBase=nightlyBase.add(rate);
        }

        for (DamageReportDto damage: rooms.getDamageReports(reservationId)) {
            if (damage.getPenaltyAmount()!=null && damage.getPenaltyAmount().compareTo(BigDecimal.ZERO)>0) {
                syncCharge(folio,FolioItemType.DAMAGE,"DAMAGE:"+damage.getId(),
                        "Damage room "+damage.getRoomNumber()+": "+damage.getItemName(),damage.getPenaltyAmount());
            }
        }

        BigDecimal early=nightlyBase.multiply(earlyCheckInRate(reservation));
        BigDecimal late=nightlyBase.multiply(lateCheckOutRate(reservation));
        syncCharge(folio,FolioItemType.TIME_SURCHARGE,"TIME:EARLY_CHECK_IN","Early check-in surcharge",early);
        syncCharge(folio,FolioItemType.TIME_SURCHARGE,"TIME:LATE_CHECK_OUT","Late check-out surcharge",late);

        List<FolioItemResponse> itemResponses=items.findByFolioId(folio.getId()).stream().map(FolioItemResponse::new).toList();
        List<PaymentTransactionResponse> paymentResponses=payments.findByFolioId(folio.getId()).stream()
                .map(PaymentTransactionResponse::new).toList();
        BigDecimal totalCharges=itemResponses.stream().map(FolioItemResponse::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal totalPaid=paymentResponses.stream().map(value -> value.getTransactionType()==TransactionType.REFUND
                ? value.getAmount().negate() : value.getAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
        long nights=reservation.getCheckInDate()==null || reservation.getCheckOutDate()==null ? 0
                : Math.max(1,ChronoUnit.DAYS.between(reservation.getCheckInDate(),reservation.getCheckOutDate()));
        return new CheckoutSummaryResponse(reservation,folio.getId(),nights,totalCharges,totalPaid,folio.getBalance(),itemResponses,paymentResponses);
    }

    public CheckoutSummaryResponse finalizeCheckout(Long reservationId) {
        CheckoutSummaryResponse summary=prepare(reservationId);
        if (!"IN_HOUSE".equals(summary.getBookingStatus())) {
            if ("CHECKED_OUT".equals(summary.getBookingStatus())) return summary;
            throw new InvalidStateException("Reservation is not IN_HOUSE");
        }
        if (summary.getBalance().compareTo(BigDecimal.ZERO)>0) {
            throw new InvalidStateException("Outstanding balance must be paid before checkout: "+summary.getBalance());
        }
        bookings.checkOut(reservationId);
        return prepare(reservationId);
    }

    private void syncCharge(Folio folio,FolioItemType type,String reference,String description,BigDecimal rawAmount) {
        BigDecimal amount=rawAmount.max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);
        FolioItem item=items.findByFolioIdAndReferenceKey(folio.getId(),reference).orElse(null);
        if (item==null) {
            if (amount.signum()==0) return;
            item=new FolioItem(); item.setFolio(folio); item.setItemType(type); item.setReferenceKey(reference);
            item.setDescription(description); item.setAmount(amount); items.save(item); folioService.adjustBalance(folio,amount);
        } else if (item.getAmount().compareTo(amount)!=0) {
            BigDecimal delta=amount.subtract(item.getAmount()); item.setAmount(amount); item.setDescription(description);
            items.save(item); folioService.adjustBalance(folio,delta);
        }
    }

    private BigDecimal earlyCheckInRate(ReservationDto reservation) {
        LocalDateTime actual=reservation.getCheckedInAt();
        if(actual==null || reservation.getCheckInDate()==null || actual.toLocalDate().isAfter(reservation.getCheckInDate())) return BigDecimal.ZERO;
        if(actual.toLocalDate().isBefore(reservation.getCheckInDate())) return BigDecimal.ONE;
        LocalTime time=actual.toLocalTime();
        if(time.isBefore(LocalTime.of(5,0))) return BigDecimal.ONE;
        if(time.isBefore(LocalTime.of(9,0))) return new BigDecimal("0.50");
        if(time.isBefore(LocalTime.of(14,0))) return new BigDecimal("0.30");
        return BigDecimal.ZERO;
    }

    private BigDecimal lateCheckOutRate(ReservationDto reservation) {
        if(reservation.getCheckOutDate()==null) return BigDecimal.ZERO;
        LocalDateTime actual=reservation.getCheckedOutAt()==null ? LocalDateTime.now() : reservation.getCheckedOutAt();
        if(actual.toLocalDate().isBefore(reservation.getCheckOutDate())) return BigDecimal.ZERO;
        if(actual.toLocalDate().isAfter(reservation.getCheckOutDate())) return BigDecimal.ONE;
        LocalTime time=actual.toLocalTime();
        if(!time.isAfter(LocalTime.NOON)) return BigDecimal.ZERO;
        if(time.isBefore(LocalTime.of(15,0))) return new BigDecimal("0.30");
        if(time.isBefore(LocalTime.of(18,0))) return new BigDecimal("0.50");
        return BigDecimal.ONE;
    }
}
