package com.hotel.room.controller;

import com.hotel.room.dto.HotelServiceRequest;
import com.hotel.room.dto.HotelServiceResponse;
import com.hotel.room.service.HotelServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/services")
@Tag(name = "Hotel Services", description = "Additional service menu (Laundry, Minibar, Spa...)")
public class HotelServiceController {

    private final HotelServiceItemService service;

    public HotelServiceController(HotelServiceItemService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all hotel services")
    public ResponseEntity<List<HotelServiceResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a hotel service by id")
    public ResponseEntity<HotelServiceResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a hotel service")
    public ResponseEntity<HotelServiceResponse> create(@Valid @RequestBody HotelServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a hotel service")
    public ResponseEntity<HotelServiceResponse> update(@PathVariable("id") Long id,
                                                        @Valid @RequestBody HotelServiceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hotel service")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
