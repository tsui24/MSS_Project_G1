package com.hotel.billing.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Mirrors the subset of booking-service's ReservationResponse fields billing-service needs to open a folio. */
public class ReservationDto {

    private Long id;
    private String bookingCode;
    private Long customerId;
    private String bookingStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private List<ReservationRoomDto> roomAssignments;

    public static class ReservationRoomDto {
        private Long id;
        private Long roomId;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        public Long getId(){return id;} public void setId(Long id){this.id=id;}
        public Long getRoomId(){return roomId;} public void setRoomId(Long roomId){this.roomId=roomId;}
        public LocalDate getCheckInDate(){return checkInDate;} public void setCheckInDate(LocalDate value){checkInDate=value;}
        public LocalDate getCheckOutDate(){return checkOutDate;} public void setCheckOutDate(LocalDate value){checkOutDate=value;}
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    public LocalDate getCheckInDate(){return checkInDate;} public void setCheckInDate(LocalDate value){checkInDate=value;}
    public LocalDate getCheckOutDate(){return checkOutDate;} public void setCheckOutDate(LocalDate value){checkOutDate=value;}
    public LocalDateTime getCheckedInAt(){return checkedInAt;} public void setCheckedInAt(LocalDateTime value){checkedInAt=value;}
    public LocalDateTime getCheckedOutAt(){return checkedOutAt;} public void setCheckedOutAt(LocalDateTime value){checkedOutAt=value;}
    public List<ReservationRoomDto> getRoomAssignments(){return roomAssignments;}
    public void setRoomAssignments(List<ReservationRoomDto> value){roomAssignments=value;}
}
