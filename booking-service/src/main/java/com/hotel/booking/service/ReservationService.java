package com.hotel.booking.service;

import com.hotel.booking.client.AuthServiceClient;
import com.hotel.booking.client.RoomServiceClient;
import com.hotel.booking.dto.ReservationRequest;
import com.hotel.booking.dto.ReservationResponse;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.exception.DuplicateResourceException;
import com.hotel.booking.exception.InvalidStateException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.ReservationRepository;
import com.hotel.booking.repository.ReservationRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final AuthServiceClient authServiceClient;
    private final RoomServiceClient roomServiceClient;

    public ReservationService(ReservationRepository reservationRepository,
                               ReservationRoomRepository reservationRoomRepository,
                               AuthServiceClient authServiceClient,
                               RoomServiceClient roomServiceClient) {
        this.reservationRepository = reservationRepository;
        this.reservationRoomRepository = reservationRoomRepository;
        this.authServiceClient = authServiceClient;
        this.roomServiceClient = roomServiceClient;
    }

    public Page<ReservationResponse> search(Long customerId, BookingStatus status, Pageable pageable) {
        Page<Reservation> page;
        if (customerId != null && status != null) {
            page = reservationRepository.findByCustomerIdAndBookingStatus(customerId, status, pageable);
        } else if (customerId != null) {
            page = reservationRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            page = reservationRepository.findByBookingStatus(status, pageable);
        } else {
            page = reservationRepository.findAll(pageable);
        }
        return page.map(ReservationResponse::new);
    }

    public ReservationResponse getById(Long id) {
        return new ReservationResponse(findEntity(id));
    }

    public ReservationResponse create(ReservationRequest request) {
        if (reservationRepository.existsByBookingCode(request.getBookingCode())) {
            throw new DuplicateResourceException("Booking code already exists: " + request.getBookingCode());
        }
        // Validates the customer really exists in auth-service before creating the reservation.
        authServiceClient.getUser(request.getCustomerId());

        Reservation reservation = new Reservation();
        reservation.setBookingCode(request.getBookingCode());
        reservation.setCustomerId(request.getCustomerId());
        reservation.setBookingStatus(BookingStatus.PENDING);
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse updateStatus(Long id, BookingStatus status) {
        Reservation reservation = findEntity(id);
        reservation.setBookingStatus(status);
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse checkIn(Long id) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() != BookingStatus.PENDING) {
            throw new InvalidStateException("Only a PENDING reservation can be checked in (current: " +
                    reservation.getBookingStatus() + ")");
        }
        reservation.setBookingStatus(BookingStatus.IN_HOUSE);
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse checkOut(Long id) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() != BookingStatus.IN_HOUSE) {
            throw new InvalidStateException("Only an IN_HOUSE reservation can be checked out (current: " +
                    reservation.getBookingStatus() + ")");
        }
        reservation.setBookingStatus(BookingStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        // Rooms need housekeeping after a guest leaves.
        releaseRooms(id, "DIRTY");
        return new ReservationResponse(reservation);
    }

    public ReservationResponse cancel(Long id) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() != BookingStatus.PENDING) {
            throw new InvalidStateException("Only a PENDING reservation can be cancelled (current: " +
                    reservation.getBookingStatus() + ")");
        }
        reservation.setBookingStatus(BookingStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Never occupied, so the room can go straight back to AVAILABLE instead of DIRTY.
        releaseRooms(id, "AVAILABLE");
        return new ReservationResponse(reservation);
    }

    public Map<BookingStatus, Long> getStatusStats() {
        Map<BookingStatus, Long> stats = new LinkedHashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            stats.put(status, 0L);
        }
        for (Object[] row : reservationRepository.countGroupedByStatus()) {
            stats.put((BookingStatus) row[0], (Long) row[1]);
        }
        return stats;
    }

    public void delete(Long id) {
        reservationRepository.delete(findEntity(id));
    }

    private void releaseRooms(Long reservationId, String roomStatus) {
        List<ReservationRoom> assignedRooms = reservationRoomRepository.findByReservationId(reservationId);
        for (ReservationRoom assignedRoom : assignedRooms) {
            roomServiceClient.updateRoomStatus(assignedRoom.getRoomId(), roomStatus);
        }
    }

    Reservation findEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
}
