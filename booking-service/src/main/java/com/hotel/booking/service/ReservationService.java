package com.hotel.booking.service;

import com.hotel.booking.client.AuthServiceClient;
import com.hotel.booking.client.RoomServiceClient;
import com.hotel.booking.dto.ReservationRequest;
import com.hotel.booking.dto.ReservationResponse;
import com.hotel.booking.dto.CheckInOccupantRequest;
import com.hotel.booking.dto.CheckInRequest;
import com.hotel.booking.dto.CheckInRoomRequest;
import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.entity.RoomOccupant;
import com.hotel.booking.exception.DuplicateResourceException;
import com.hotel.booking.exception.InvalidStateException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.ReservationRepository;
import com.hotel.booking.repository.ReservationRoomRepository;
import com.hotel.booking.repository.RoomOccupantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final AuthServiceClient authServiceClient;
    private final RoomServiceClient roomServiceClient;
    private final RoomOccupantRepository roomOccupantRepository;

    public ReservationService(ReservationRepository reservationRepository,
                               ReservationRoomRepository reservationRoomRepository,
                               AuthServiceClient authServiceClient,
                               RoomServiceClient roomServiceClient,
                               RoomOccupantRepository roomOccupantRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationRoomRepository = reservationRoomRepository;
        this.authServiceClient = authServiceClient;
        this.roomServiceClient = roomServiceClient;
        this.roomOccupantRepository = roomOccupantRepository;
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
        return page.map(this::toResponse);
    }

    public ReservationResponse getById(Long id) {
        return toResponse(findEntity(id));
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
        return toResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse updateStatus(Long id, BookingStatus status) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() == status) return toResponse(reservation);
        return switch (status) {
            case IN_HOUSE -> throw new InvalidStateException(
                    "Use the check-in endpoint with room assignments and occupant information");
            case CHECKED_OUT -> checkOut(id);
            case CANCELLED -> cancel(id);
            case PENDING -> throw new InvalidStateException("A reservation cannot transition back to PENDING");
        };
    }

    @Transactional
    public ReservationResponse checkIn(Long id, CheckInRequest request) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() == BookingStatus.IN_HOUSE) {
            if (matchesExistingCheckIn(id, request)) return toResponse(reservation);
            throw new InvalidStateException("Reservation is already IN_HOUSE with different check-in data");
        }
        if (reservation.getBookingStatus() != BookingStatus.PENDING) {
            throw new InvalidStateException("Only a PENDING reservation can be checked in (current: " +
                    reservation.getBookingStatus() + ")");
        }
        List<ReservationRoom> assignedRooms = reservationRoomRepository.findByReservationId(id);
        if (assignedRooms.isEmpty()) {
            throw new InvalidStateException("Cannot check in a reservation without assigned rooms");
        }
        Map<Long, CheckInRoomRequest> requestedByAssignment = validateCheckInRequest(
                reservation, assignedRooms, request);

        for (ReservationRoom assignment : assignedRooms) {
            CheckInRoomRequest requestedRoom = requestedByAssignment.get(assignment.getId());
            assignment.setRoomId(requestedRoom.getRoomId());
            reservationRoomRepository.save(assignment);
            roomOccupantRepository.deleteByReservationRoomId(assignment.getId());
            for (CheckInOccupantRequest requestedOccupant : requestedRoom.getOccupants()) {
                roomOccupantRepository.save(toOccupant(assignment, requestedOccupant));
            }
        }

        reservation.setBookingStatus(BookingStatus.IN_HOUSE);
        reservation.setCheckedInAt(LocalDateTime.now());
        reservationRepository.saveAndFlush(reservation);
        roomOccupantRepository.flush();
        setRoomStatusesWithCompensation(assignedRooms, "OCCUPIED", "AVAILABLE");
        registerCheckInRollbackCompensation(assignedRooms);
        return toResponse(reservation);
    }

    public ReservationResponse checkOut(Long id) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() != BookingStatus.IN_HOUSE) {
            throw new InvalidStateException("Only an IN_HOUSE reservation can be checked out (current: " +
                    reservation.getBookingStatus() + ")");
        }
        reservation.setBookingStatus(BookingStatus.CHECKED_OUT);
        reservation.setCheckedOutAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        // Rooms need housekeeping after a guest leaves.
        releaseRooms(id, "DIRTY");
        return toResponse(reservation);
    }

    public ReservationResponse cancel(Long id) {
        Reservation reservation = findEntity(id);
        if (reservation.getBookingStatus() != BookingStatus.PENDING) {
            throw new InvalidStateException("Only a PENDING reservation can be cancelled (current: " +
                    reservation.getBookingStatus() + ")");
        }
        reservation.setBookingStatus(BookingStatus.CANCELLED);
        reservationRepository.save(reservation);

        // PENDING reservations do not own the physical room status. Availability for
        // future dates is controlled by reservation-room overlap, so cancellation must
        // not overwrite DIRTY/CLEANING/MAINTENANCE state.
        return toResponse(reservation);
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

    @Transactional
    public void delete(Long id) {
        Reservation reservation = findEntity(id);
        // Reservation creation is a multi-request flow in the clients. Removing the
        // child assignments first lets a failed flow roll back without leaving an
        // empty reservation or violating the reservation_rooms foreign key.
        reservationRoomRepository.deleteAll(reservationRoomRepository.findByReservationId(id));
        reservationRoomRepository.flush();
        reservationRepository.delete(reservation);
    }

    private void releaseRooms(Long reservationId, String roomStatus) {
        List<ReservationRoom> assignedRooms = reservationRoomRepository.findByReservationId(reservationId);
        for (ReservationRoom assignedRoom : assignedRooms) {
            roomServiceClient.updateRoomStatus(assignedRoom.getRoomId(), roomStatus);
        }
    }

    public long reconcileInHouseRoomStatuses() {
        long updated = 0;
        for (Reservation reservation : reservationRepository.findByBookingStatus(BookingStatus.IN_HOUSE)) {
            for (ReservationRoom assignedRoom : reservationRoomRepository.findByReservationId(reservation.getId())) {
                if (!"OCCUPIED".equals(roomServiceClient.getRoom(assignedRoom.getRoomId()).getStatus())) {
                    roomServiceClient.updateRoomStatus(assignedRoom.getRoomId(), "OCCUPIED");
                    updated++;
                }
            }
        }
        return updated;
    }

    private void setRoomStatusesWithCompensation(List<ReservationRoom> assignedRooms,
                                                   String targetStatus,
                                                   String rollbackStatus) {
        int updated = 0;
        try {
            for (ReservationRoom assignedRoom : assignedRooms) {
                if ("OCCUPIED".equals(targetStatus)) {
                    roomServiceClient.occupyIfAvailable(assignedRoom.getRoomId());
                } else {
                    roomServiceClient.updateRoomStatus(assignedRoom.getRoomId(), targetStatus);
                }
                updated++;
            }
        } catch (RuntimeException exception) {
            restoreRoomStatuses(assignedRooms.subList(0, updated), rollbackStatus);
            throw exception;
        }
    }

    private void restoreRoomStatuses(List<ReservationRoom> assignedRooms, String status) {
        for (ReservationRoom assignedRoom : assignedRooms) {
            try {
                if ("AVAILABLE".equals(status)) {
                    roomServiceClient.compensateFailedCheckIn(assignedRoom.getRoomId());
                } else {
                    roomServiceClient.updateRoomStatus(assignedRoom.getRoomId(), status);
                }
            } catch (RuntimeException ignored) {
                // Best-effort compensation. The reconcile endpoint repairs IN_HOUSE rooms.
            }
        }
    }

    private Map<Long, CheckInRoomRequest> validateCheckInRequest(Reservation reservation,
                                                                  List<ReservationRoom> assignments,
                                                                  CheckInRequest request) {
        if (request.getRoomAssignments().size() != assignments.size()) {
            throw new InvalidStateException("Check-in must include every reservation room exactly once");
        }

        Map<Long, CheckInRoomRequest> requestedByAssignment = new HashMap<>();
        Set<Long> requestedRoomIds = new HashSet<>();
        Set<String> identityDocuments = new HashSet<>();
        for (CheckInRoomRequest requested : request.getRoomAssignments()) {
            if (requestedByAssignment.put(requested.getReservationRoomId(), requested) != null) {
                throw new InvalidStateException("Duplicate reservationRoomId: " + requested.getReservationRoomId());
            }
            if (!requestedRoomIds.add(requested.getRoomId())) {
                throw new InvalidStateException("The same physical room cannot be assigned twice");
            }
            for (CheckInOccupantRequest occupant : requested.getOccupants()) {
                String identity = occupant.getIdentityDocument().trim().toUpperCase();
                if (!identityDocuments.add(identity)) {
                    throw new InvalidStateException("Duplicate identity document in check-in: " + identity);
                }
            }
        }

        for (ReservationRoom assignment : assignments) {
            CheckInRoomRequest requested = requestedByAssignment.get(assignment.getId());
            if (requested == null) {
                throw new InvalidStateException("Missing reservation room assignment: " + assignment.getId());
            }
            int expectedGuests = assignment.getGuestCount() == null ? 0 : assignment.getGuestCount();
            if (expectedGuests <= 0) {
                throw new InvalidStateException("Reservation room " + assignment.getId()
                        + " has no valid guest count; correct the legacy reservation before check-in");
            }
            if (requested.getOccupants().size() != expectedGuests) {
                throw new InvalidStateException("Reservation room " + assignment.getId() + " requires "
                        + expectedGuests + " occupants but received " + requested.getOccupants().size());
            }

            RoomDto originalRoom = roomServiceClient.getRoom(assignment.getRoomId());
            RoomDto selectedRoom = roomServiceClient.getRoom(requested.getRoomId());
            Long originalClassId = originalRoom.getRoomClass() == null ? null : originalRoom.getRoomClass().getId();
            Long selectedClassId = selectedRoom.getRoomClass() == null ? null : selectedRoom.getRoomClass().getId();
            if (originalClassId == null || !originalClassId.equals(selectedClassId)) {
                throw new InvalidStateException("Selected room " + selectedRoom.getRoomNumber()
                        + " does not match the reserved room class");
            }
            if (!"AVAILABLE".equals(selectedRoom.getStatus())) {
                throw new InvalidStateException("Room " + selectedRoom.getRoomNumber()
                        + " is not AVAILABLE (current: " + selectedRoom.getStatus() + ")");
            }
            if (!reservationRoomRepository.findConflictsExcludingReservation(
                    requested.getRoomId(), reservation.getId(), assignment.getCheckInDate(),
                    assignment.getCheckOutDate()).isEmpty()) {
                throw new InvalidStateException("Room " + selectedRoom.getRoomNumber()
                        + " has another overlapping reservation");
            }
        }
        return requestedByAssignment;
    }

    private RoomOccupant toOccupant(ReservationRoom assignment, CheckInOccupantRequest requested) {
        RoomOccupant occupant = new RoomOccupant();
        occupant.setReservationRoom(assignment);
        occupant.setGuestName(requested.getGuestName().trim());
        occupant.setPhoneNumber(requested.getPhoneNumber().trim());
        occupant.setIdentityDocument(requested.getIdentityDocument().trim());
        occupant.setResidence(requested.getResidence().trim());
        return occupant;
    }

    private boolean matchesExistingCheckIn(Long reservationId, CheckInRequest request) {
        List<ReservationRoom> assignments = reservationRoomRepository.findByReservationId(reservationId);
        if (request.getRoomAssignments().size() != assignments.size()) return false;
        Map<Long, CheckInRoomRequest> requested = new HashMap<>();
        for (CheckInRoomRequest room : request.getRoomAssignments()) {
            if (requested.put(room.getReservationRoomId(), room) != null) return false;
        }
        for (ReservationRoom assignment : assignments) {
            CheckInRoomRequest room = requested.get(assignment.getId());
            if (room == null || !assignment.getRoomId().equals(room.getRoomId())) return false;
            List<RoomOccupant> saved = roomOccupantRepository.findByReservationRoomId(assignment.getId());
            if (saved.size() != room.getOccupants().size()) return false;
            Set<String> savedGuests = saved.stream().map(this::occupantFingerprint)
                    .collect(java.util.stream.Collectors.toSet());
            Set<String> requestedGuests = room.getOccupants().stream().map(this::occupantFingerprint)
                    .collect(java.util.stream.Collectors.toSet());
            if (!savedGuests.equals(requestedGuests)) return false;
        }
        return true;
    }

    private String occupantFingerprint(RoomOccupant occupant) {
        return normalized(occupant.getGuestName()) + "|" + normalized(occupant.getPhoneNumber()) + "|"
                + normalized(occupant.getIdentityDocument()) + "|" + normalized(occupant.getResidence());
    }

    private String occupantFingerprint(CheckInOccupantRequest occupant) {
        return normalized(occupant.getGuestName()) + "|" + normalized(occupant.getPhoneNumber()) + "|"
                + normalized(occupant.getIdentityDocument()) + "|" + normalized(occupant.getResidence());
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private void registerCheckInRollbackCompensation(List<ReservationRoom> assignments) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        List<Long> roomIds = assignments.stream().map(ReservationRoom::getRoomId).toList();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_ROLLED_BACK) return;
                for (Long roomId : roomIds) {
                    try {
                        roomServiceClient.compensateFailedCheckIn(roomId);
                    } catch (RuntimeException ignored) {
                        // The lifecycle reconcile script is the final repair mechanism.
                    }
                }
            }
        });
    }

    Reservation findEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse(reservation);
        response.setRoomAssignments(reservationRoomRepository.findByReservationId(reservation.getId()));
        try {
            response.setCustomer(authServiceClient.getUser(reservation.getCustomerId()));
        } catch (RuntimeException ignored) {
            // A temporary auth-service outage must not make the reservation list unavailable.
        }
        return response;
    }
}
