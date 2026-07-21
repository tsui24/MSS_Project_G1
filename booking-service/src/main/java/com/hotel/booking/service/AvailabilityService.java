package com.hotel.booking.service;

import com.hotel.booking.client.RoomServiceClient;
import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.exception.InvalidStateException;
import com.hotel.booking.repository.ReservationRoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final RoomServiceClient roomServiceClient;
    private final ReservationRoomRepository reservationRoomRepository;

    public AvailabilityService(RoomServiceClient roomServiceClient, ReservationRoomRepository reservationRoomRepository) {
        this.roomServiceClient = roomServiceClient;
        this.reservationRoomRepository = reservationRoomRepository;
    }

    /** Rooms of the given class (or all rooms, if roomClassId is null) with no overlapping booking in the date range. */
    public List<RoomDto> findAvailableRooms(Long roomClassId, LocalDate checkInDate, LocalDate checkOutDate) {
        return findAvailableRooms(roomClassId, checkInDate, checkOutDate, null);
    }

    /** Rooms of the given class (or all rooms, if roomClassId is null) with no overlapping booking in the date range,
     * optionally excluding rooms that belong to a specific reservation (used when editing a reservation). */
    public List<RoomDto> findAvailableRooms(Long roomClassId, LocalDate checkInDate, LocalDate checkOutDate,
                                            Long excludeReservationId) {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidStateException("checkOutDate must be after checkInDate");
        }

        List<RoomDto> candidateRooms = roomServiceClient.getRooms(roomClassId);
        if (candidateRooms.isEmpty()) {
            return List.of();
        }

        List<Long> roomIds = candidateRooms.stream().map(RoomDto::getId).toList();
        Set<Long> bookedRoomIds = reservationRoomRepository.findOverlapping(roomIds, checkInDate, checkOutDate, excludeReservationId)
                .stream().map(ReservationRoom::getRoomId).collect(Collectors.toSet());

        return candidateRooms.stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .filter(room -> isBookableForDate(room, checkInDate))
                .toList();
    }

    private boolean isBookableForDate(RoomDto room, LocalDate checkInDate) {
        if ("AVAILABLE".equals(room.getStatus())) {
            return true;
        }
        // Physical status describes the room right now, not on a future arrival date.
        // For a future stay, OCCUPIED/DIRTY/CLEANING rooms remain sellable when the
        // reservation-overlap query proves that the previous stay ends by check-in.
        // MAINTENANCE remains blocked until an administrator explicitly releases it.
        return checkInDate.isAfter(LocalDate.now()) && !"MAINTENANCE".equals(room.getStatus());
    }
}
