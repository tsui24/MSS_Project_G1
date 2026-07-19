package com.hotel.room.controller;
import com.hotel.room.dto.*;
import com.hotel.room.service.DamageReportService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/damage-reports")
public class DamageReportController {
    private final DamageReportService service;
    public DamageReportController(DamageReportService service){this.service=service;}
    @GetMapping public List<DamageReportResponse> find(@RequestParam(name="staffId",required=false) Long staffId,
                                                       @RequestParam(name="reservationId",required=false) Long reservationId){
        return service.find(staffId, reservationId);
    }
    @PostMapping public ResponseEntity<DamageReportResponse> create(@Valid @RequestBody DamageReportRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));}
}
