package com.hotel.room.service;

import com.hotel.room.dto.RoomClassRequest;
import com.hotel.room.dto.RoomClassResponse;
import com.hotel.room.entity.RoomClass;
import com.hotel.room.exception.DuplicateResourceException;
import com.hotel.room.exception.ResourceNotFoundException;
import com.hotel.room.repository.RoomClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomClassService {

    private final RoomClassRepository roomClassRepository;

    public RoomClassService(RoomClassRepository roomClassRepository) {
        this.roomClassRepository = roomClassRepository;
    }

    public List<RoomClassResponse> getAll() {
        return roomClassRepository.findAll().stream().map(RoomClassResponse::new).toList();
    }

    public RoomClassResponse getById(Long id) {
        return new RoomClassResponse(findEntity(id));
    }

    @Transactional
    public RoomClassResponse create(RoomClassRequest request) {
        if (roomClassRepository.existsByClassName(request.getClassName())) {
            throw new DuplicateResourceException("Room class already exists: " + request.getClassName());
        }
        RoomClass roomClass = new RoomClass();
        applyRequest(roomClass, request, true);
        return new RoomClassResponse(roomClassRepository.save(roomClass));
    }

    @Transactional
    public RoomClassResponse update(Long id, RoomClassRequest request) {
        RoomClass roomClass = findEntity(id);
        applyRequest(roomClass, request, false);
        return new RoomClassResponse(roomClassRepository.save(roomClass));
    }

    public void delete(Long id) {
        roomClassRepository.delete(findEntity(id));
    }

    private void applyRequest(RoomClass roomClass, RoomClassRequest request, boolean creating) {
        roomClass.setClassName(request.getClassName());
        roomClass.setBasePrice(request.getBasePrice());
        roomClass.setStandardOccupancy(request.getStandardOccupancy());
        roomClass.setMaxOccupancy(request.getMaxOccupancy());
        roomClass.setExtraPersonFee(request.getExtraPersonFee());
        if (request.getAmenities() != null) {
            roomClass.setAmenities(new ArrayList<>(request.getAmenities()));
        } else if (creating) {
            roomClass.setAmenities(new ArrayList<>());
        }
    }

    RoomClass findEntity(Long id) {
        return roomClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room class not found with id: " + id));
    }
}
