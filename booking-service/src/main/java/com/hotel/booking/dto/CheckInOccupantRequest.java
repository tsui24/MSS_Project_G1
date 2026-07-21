package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInOccupantRequest {
    @NotBlank private String guestName;
    private String phoneNumber;      // Optional
    @NotBlank private String identityDocument;
    private String residence;         // Optional

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getIdentityDocument() { return identityDocument; }
    public void setIdentityDocument(String identityDocument) { this.identityDocument = identityDocument; }
    public String getResidence() { return residence; }
    public void setResidence(String residence) { this.residence = residence; }
}
