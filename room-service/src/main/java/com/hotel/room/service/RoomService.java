package com.hotel.room.service;

import com.hotel.room.dto.RoomRequest;
import com.hotel.room.dto.RoomResponse;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomClass;
import com.hotel.room.entity.RoomStatus;
import com.hotel.room.exception.DuplicateResourceException;
import com.hotel.room.exception.ResourceNotFoundException;
import com.hotel.room.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomClassService roomClassService;

    public RoomService(RoomRepository roomRepository, RoomClassService roomClassService) {
        this.roomRepository = roomRepository;
        this.roomClassService = roomClassService;
    }

    public Page<RoomResponse> search(RoomStatus status, Long roomClassId, Pageable pageable) {
        Page<Room> page;
        if (status != null && roomClassId != null) {
            page = roomRepository.findByStatusAndRoomClass_Id(status, roomClassId, pageable);
        } else if (status != null) {
            page = roomRepository.findByStatus(status, pageable);
        } else if (roomClassId != null) {
            page = roomRepository.findByRoomClass_Id(roomClassId, pageable);
        } else {
            page = roomRepository.findAll(pageable);
        }
        return page.map(RoomResponse::new);
    }

    public RoomResponse getById(Long id) {
        return new RoomResponse(findEntity(id));
    }

    public RoomResponse create(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists: " + request.getRoomNumber());
        }
        RoomClass roomClass = roomClassService.findEntity(request.getRoomClassId());

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomClass(roomClass);
        room.setStatus(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE);
        return new RoomResponse(roomRepository.save(room));
    }

    public RoomResponse update(Long id, RoomRequest request) {
        Room room = findEntity(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomClass(roomClassService.findEntity(request.getRoomClassId()));
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        return new RoomResponse(roomRepository.save(room));
    }

    public RoomResponse updateStatus(Long id, RoomStatus status) {
        Room room = findEntity(id);
        room.setStatus(status);
        return new RoomResponse(roomRepository.save(room));
    }

    public void delete(Long id) {
        roomRepository.delete(findEntity(id));
    }

    private Room findEntity(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }
}
