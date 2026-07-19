package com.hotel.booking.service;

import com.hotel.booking.client.AuthServiceClient;
import com.hotel.booking.client.RoomServiceClient;
import com.hotel.booking.dto.CheckInOccupantRequest;
import com.hotel.booking.dto.CheckInRequest;
import com.hotel.booking.dto.CheckInRoomRequest;
import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.entity.RoomOccupant;
import com.hotel.booking.exception.InvalidStateException;
import com.hotel.booking.repository.ReservationRepository;
import com.hotel.booking.repository.ReservationRoomRepository;
import com.hotel.booking.repository.RoomOccupantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceCheckInTest {
    private ReservationRepository reservations;
    private ReservationRoomRepository assignments;
    private RoomServiceClient rooms;
    private RoomOccupantRepository occupants;
    private ReservationService service;

    @BeforeEach
    void setUp() {
        reservations = mock(ReservationRepository.class);
        assignments = mock(ReservationRoomRepository.class);
        rooms = mock(RoomServiceClient.class);
        occupants = mock(RoomOccupantRepository.class);
        service = new ReservationService(reservations, assignments, mock(AuthServiceClient.class), rooms, occupants);
    }

    @Test
    void checkInPersistsAllOccupantsAndOccupiesRoom() {
        Reservation reservation = reservation(BookingStatus.PENDING);
        ReservationRoom assignment = assignment(reservation, 2);
        CheckInRequest request = request(assignment.getId(), assignment.getRoomId(),
                occupant("Guest One", "ID-1"), occupant("Guest Two", "ID-2"));
        stubReservation(reservation, assignment);
        when(rooms.getRoom(assignment.getRoomId())).thenReturn(room(assignment.getRoomId(), "AVAILABLE", 3L));
        when(assignments.findConflictsExcludingReservation(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        service.checkIn(reservation.getId(), request);

        assertEquals(BookingStatus.IN_HOUSE, reservation.getBookingStatus());
        verify(occupants, times(2)).save(any(RoomOccupant.class));
        verify(rooms).occupyIfAvailable(assignment.getRoomId());
        verify(reservations).saveAndFlush(reservation);
    }

    @Test
    void checkInRejectsDuplicateIdentityBeforeWritingAnything() {
        Reservation reservation = reservation(BookingStatus.PENDING);
        ReservationRoom assignment = assignment(reservation, 2);
        stubReservation(reservation, assignment);
        CheckInRequest request = request(assignment.getId(), assignment.getRoomId(),
                occupant("Guest One", "same-id"), occupant("Guest Two", " SAME-ID "));

        assertThrows(InvalidStateException.class, () -> service.checkIn(reservation.getId(), request));

        verifyNoInteractions(rooms);
        verify(occupants, never()).save(any());
        verify(reservations, never()).saveAndFlush(any());
    }

    @Test
    void identicalRetryForInHouseReservationIsIdempotent() {
        Reservation reservation = reservation(BookingStatus.IN_HOUSE);
        ReservationRoom assignment = assignment(reservation, 1);
        CheckInOccupantRequest requestedGuest = occupant("Guest One", "ID-1");
        CheckInRequest request = request(assignment.getId(), assignment.getRoomId(), requestedGuest);
        RoomOccupant savedGuest = new RoomOccupant();
        savedGuest.setReservationRoom(assignment);
        savedGuest.setGuestName(requestedGuest.getGuestName());
        savedGuest.setPhoneNumber(requestedGuest.getPhoneNumber());
        savedGuest.setIdentityDocument(requestedGuest.getIdentityDocument());
        savedGuest.setResidence(requestedGuest.getResidence());
        stubReservation(reservation, assignment);
        when(occupants.findByReservationRoomId(assignment.getId())).thenReturn(List.of(savedGuest));

        service.checkIn(reservation.getId(), request);

        verifyNoInteractions(rooms);
        verify(reservations, never()).saveAndFlush(any());
        verify(occupants, never()).save(any());
    }

    private void stubReservation(Reservation reservation, ReservationRoom assignment) {
        when(reservations.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        when(assignments.findByReservationId(reservation.getId())).thenReturn(List.of(assignment));
    }

    private Reservation reservation(BookingStatus status) {
        Reservation value = new Reservation();
        value.setId(10L);
        value.setBookingCode("RES-TEST");
        value.setCustomerId(5L);
        value.setBookingStatus(status);
        return value;
    }

    private ReservationRoom assignment(Reservation reservation, int guestCount) {
        ReservationRoom value = new ReservationRoom();
        value.setId(20L);
        value.setReservation(reservation);
        value.setRoomId(30L);
        value.setGuestCount(guestCount);
        value.setCheckInDate(LocalDate.of(2026, 7, 20));
        value.setCheckOutDate(LocalDate.of(2026, 7, 21));
        return value;
    }

    private RoomDto room(Long id, String status, Long roomClassId) {
        RoomDto value = new RoomDto();
        value.setId(id);
        value.setRoomNumber("101");
        value.setStatus(status);
        RoomDto.RoomClassDto roomClass = new RoomDto.RoomClassDto();
        roomClass.setId(roomClassId);
        value.setRoomClass(roomClass);
        return value;
    }

    private CheckInOccupantRequest occupant(String name, String identity) {
        CheckInOccupantRequest value = new CheckInOccupantRequest();
        value.setGuestName(name);
        value.setPhoneNumber("0900000000");
        value.setIdentityDocument(identity);
        value.setResidence("Ha Noi");
        return value;
    }

    private CheckInRequest request(Long assignmentId, Long roomId, CheckInOccupantRequest... guests) {
        CheckInRoomRequest room = new CheckInRoomRequest();
        room.setReservationRoomId(assignmentId);
        room.setRoomId(roomId);
        room.setOccupants(List.of(guests));
        CheckInRequest request = new CheckInRequest();
        request.setRoomAssignments(List.of(room));
        return request;
    }
}
