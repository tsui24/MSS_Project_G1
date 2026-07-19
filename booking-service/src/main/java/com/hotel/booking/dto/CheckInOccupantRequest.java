package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInOccupantRequest {
    @NotBlank private String guestName;
    @NotBlank private String phoneNumber;
    @NotBlank private String identityDocument;
    @NotBlank private String residence;

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getIdentityDocument() { return identityDocument; }
    public void setIdentityDocument(String identityDocument) { this.identityDocument = identityDocument; }
    public String getResidence() { return residence; }
    public void setResidence(String residence) { this.residence = residence; }
}
