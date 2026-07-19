package com.hotel.room.controller;

import com.hotel.room.dto.HousekeepingTaskRequest;
import com.hotel.room.dto.HousekeepingTaskResponse;
import com.hotel.room.dto.TaskStatusRequest;
import com.hotel.room.service.HousekeepingTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/housekeeping-tasks")
public class HousekeepingTaskController {
    private final HousekeepingTaskService service;
    public HousekeepingTaskController(HousekeepingTaskService service) { this.service = service; }

    @GetMapping
    public List<HousekeepingTaskResponse> findAll(@RequestParam(name = "staffId", required = false) Long staffId) {
        return service.findAll(staffId);
    }
    @GetMapping("/{id}")
    public HousekeepingTaskResponse get(@PathVariable("id") Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<HousekeepingTaskResponse> create(@Valid @RequestBody HousekeepingTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @PatchMapping("/{id}/status")
    public HousekeepingTaskResponse updateStatus(@PathVariable("id") Long id, @Valid @RequestBody TaskStatusRequest request) {
        return service.updateStatus(id, request.getStatus());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
