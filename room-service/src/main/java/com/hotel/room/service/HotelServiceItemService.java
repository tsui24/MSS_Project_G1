package com.hotel.room.service;

import com.hotel.room.dto.HotelServiceRequest;
import com.hotel.room.dto.HotelServiceResponse;
import com.hotel.room.entity.HotelServiceItem;
import com.hotel.room.exception.DuplicateResourceException;
import com.hotel.room.exception.ResourceNotFoundException;
import com.hotel.room.repository.HotelServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceItemService {

    private final HotelServiceItemRepository repository;

    public HotelServiceItemService(HotelServiceItemRepository repository) {
        this.repository = repository;
    }

    public List<HotelServiceResponse> getAll() {
        return repository.findAll().stream().map(HotelServiceResponse::new).toList();
    }

    public HotelServiceResponse getById(Long id) {
        return new HotelServiceResponse(findEntity(id));
    }

    public HotelServiceResponse create(HotelServiceRequest request) {
        if (repository.existsByServiceName(request.getServiceName())) {
            throw new DuplicateResourceException("Service already exists: " + request.getServiceName());
        }
        HotelServiceItem item = new HotelServiceItem();
        item.setServiceName(request.getServiceName());
        item.setUnitPrice(request.getUnitPrice());
        item.setCategory(request.getCategory());
        item.setDescription(request.getDescription());
        return new HotelServiceResponse(repository.save(item));
    }

    public HotelServiceResponse update(Long id, HotelServiceRequest request) {
        HotelServiceItem item = findEntity(id);
        item.setServiceName(request.getServiceName());
        item.setUnitPrice(request.getUnitPrice());
        item.setCategory(request.getCategory());
        item.setDescription(request.getDescription());
        return new HotelServiceResponse(repository.save(item));
    }

    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    private HotelServiceItem findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel service not found with id: " + id));
    }
}
