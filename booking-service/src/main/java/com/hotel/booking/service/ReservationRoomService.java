package com.hotel.booking.service;

import com.hotel.booking.client.RoomServiceClient;
import com.hotel.booking.dto.ReservationRoomRequest;
import com.hotel.booking.dto.ReservationRoomResponse;
import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.entity.Reservation;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.exception.InvalidStateException;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.ReservationRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final ReservationService reservationService;
    private final RoomServiceClient roomServiceClient;

    public ReservationRoomService(ReservationRoomRepository reservationRoomRepository,
                                   ReservationService reservationService,
                                   RoomServiceClient roomServiceClient) {
        this.reservationRoomRepository = reservationRoomRepository;
        this.reservationService = reservationService;
        this.roomServiceClient = roomServiceClient;
    }

    public List<ReservationRoomResponse> getByReservationId(Long reservationId) {
        return reservationRoomRepository.findByReservationId(reservationId).stream()
                .map(ReservationRoomResponse::new).toList();
    }

    public ReservationRoomResponse getById(Long id) {
        return new ReservationRoomResponse(findEntity(id));
    }

    public ReservationRoomResponse assignRoom(ReservationRoomRequest request) {
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new InvalidStateException("checkOutDate must be after checkInDate");
        }

        Reservation reservation = reservationService.findEntity(request.getReservationId());

        RoomDto room = roomServiceClient.getRoom(request.getRoomId());
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new InvalidStateException("Room " + room.getRoomNumber() + " is not AVAILABLE (current status: " + room.getStatus() + ")");
        }

        List<ReservationRoom> overlapping = reservationRoomRepository.findOverlapping(
                List.of(request.getRoomId()), request.getCheckInDate(), request.getCheckOutDate());
        if (!overlapping.isEmpty()) {
            throw new InvalidStateException("Room " + room.getRoomNumber() +
                    " is already booked for an overlapping date range");
        }

        ReservationRoom reservationRoom = new ReservationRoom();
        reservationRoom.setReservation(reservation);
        reservationRoom.setRoomId(request.getRoomId());
        reservationRoom.setCheckInDate(request.getCheckInDate());
        reservationRoom.setCheckOutDate(request.getCheckOutDate());
        ReservationRoom saved = reservationRoomRepository.save(reservationRoom);

        roomServiceClient.updateRoomStatus(request.getRoomId(), "OCCUPIED");

        return new ReservationRoomResponse(saved);
    }

    public void release(Long id) {
        ReservationRoom reservationRoom = findEntity(id);
        roomServiceClient.updateRoomStatus(reservationRoom.getRoomId(), "DIRTY");
        reservationRoomRepository.delete(reservationRoom);
    }

    ReservationRoom findEntity(Long id) {
        return reservationRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation-room assignment not found with id: " + id));
    }
}
