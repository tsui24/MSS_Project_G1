package com.hotel.booking.service;

import com.hotel.booking.dto.RoomOccupantRequest;
import com.hotel.booking.dto.RoomOccupantResponse;
import com.hotel.booking.entity.ReservationRoom;
import com.hotel.booking.entity.RoomOccupant;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.repository.RoomOccupantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomOccupantService {

    private final RoomOccupantRepository roomOccupantRepository;
    private final ReservationRoomService reservationRoomService;

    public RoomOccupantService(RoomOccupantRepository roomOccupantRepository,
                                ReservationRoomService reservationRoomService) {
        this.roomOccupantRepository = roomOccupantRepository;
        this.reservationRoomService = reservationRoomService;
    }

    @Transactional(readOnly = true)
    public List<RoomOccupantResponse> getByReservationRoomId(Long reservationRoomId) {
        return roomOccupantRepository.findByReservationRoomId(reservationRoomId).stream()
                .map(RoomOccupantResponse::new).toList();
    }

    public RoomOccupantResponse create(RoomOccupantRequest request) {
        ReservationRoom reservationRoom = reservationRoomService.findEntity(request.getReservationRoomId());

        RoomOccupant occupant = new RoomOccupant();
        occupant.setReservationRoom(reservationRoom);
        occupant.setGuestName(request.getGuestName());
        occupant.setPhoneNumber(request.getPhoneNumber());
        occupant.setIdentityDocument(request.getIdentityDocument());
        occupant.setResidence(request.getResidence());
        return new RoomOccupantResponse(roomOccupantRepository.save(occupant));
    }

    public void delete(Long id) {
        RoomOccupant occupant = roomOccupantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room occupant not found with id: " + id));
        roomOccupantRepository.delete(occupant);
    }
}
