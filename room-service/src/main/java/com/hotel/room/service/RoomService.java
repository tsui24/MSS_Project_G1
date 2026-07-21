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
import org.springframework.transaction.annotation.Transactional;

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
        room.setDescription(request.getDescription());
        room.setFloor(request.getFloor());
        return new RoomResponse(roomRepository.save(room));
    }

    public RoomResponse update(Long id, RoomRequest request) {
        Room room = findEntity(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomClass(roomClassService.findEntity(request.getRoomClassId()));
        if (request.getStatus() != null) {
            validateStatusTransition(room.getStatus(), request.getStatus());
            room.setStatus(request.getStatus());
        }
        room.setDescription(request.getDescription());
        room.setFloor(request.getFloor());
        return new RoomResponse(roomRepository.save(room));
    }

    public RoomResponse updateStatus(Long id, RoomStatus status) {
        Room room = findEntity(id);
        validateStatusTransition(room.getStatus(), status);
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

    public RoomResponse compensateFailedCheckIn(Long id) {
        Room room = findEntity(id);
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            room.setStatus(RoomStatus.AVAILABLE);
        } else if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException("Cannot compensate check-in from room status " + room.getStatus());
        }
        return new RoomResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse occupyIfAvailable(Long id) {
        Room room = roomRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber()
                    + " is no longer AVAILABLE (current: " + room.getStatus() + ")");
        }
        room.setStatus(RoomStatus.OCCUPIED);
        return new RoomResponse(roomRepository.save(room));
    }

    private void validateStatusTransition(RoomStatus current, RoomStatus target) {
        if (current == target || target == RoomStatus.OCCUPIED) return;

        boolean allowed = switch (current) {
            case AVAILABLE -> target == RoomStatus.DIRTY || target == RoomStatus.MAINTENANCE;
            case OCCUPIED -> target == RoomStatus.DIRTY;
            case DIRTY -> target == RoomStatus.CLEANING || target == RoomStatus.AVAILABLE
                    || target == RoomStatus.MAINTENANCE;
            case CLEANING -> target == RoomStatus.AVAILABLE || target == RoomStatus.MAINTENANCE;
            case MAINTENANCE -> target == RoomStatus.AVAILABLE || target == RoomStatus.DIRTY;
        };
        if (!allowed) {
            throw new IllegalArgumentException("Invalid room status transition: " + current + " -> " + target);
        }
    }
}
