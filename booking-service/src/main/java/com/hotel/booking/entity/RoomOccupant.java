package com.hotel.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "room_occupants")
public class RoomOccupant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_room_id", nullable = false)
    private ReservationRoom reservationRoom;

    @Column(name = "guest_name", nullable = false, length = 150)
    private String guestName;

    @Column(name = "phone_number", nullable = true, length = 30)
    private String phoneNumber;

    @Column(name = "identity_document", nullable = false, length = 50)
    private String identityDocument;

    @Column(name = "residence", nullable = true, length = 255)
    private String residence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationRoom getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(ReservationRoom reservationRoom) {
        this.reservationRoom = reservationRoom;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getIdentityDocument() { return identityDocument; }
    public void setIdentityDocument(String identityDocument) { this.identityDocument = identityDocument; }
    public String getResidence() { return residence; }
    public void setResidence(String residence) { this.residence = residence; }
}
