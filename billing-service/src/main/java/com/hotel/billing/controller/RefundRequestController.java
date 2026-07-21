package com.hotel.billing.controller;

import com.hotel.billing.dto.RefundRequestResponse;
import com.hotel.billing.service.RefundRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/billing/refunds")
public class RefundRequestController {
    private final RefundRequestService service;
    public RefundRequestController(RefundRequestService service) { this.service = service; }

    @GetMapping("/pending")
    public ResponseEntity<Page<RefundRequestResponse>> pending(Pageable pageable) { return ResponseEntity.ok(service.pending(pageable)); }
    @GetMapping("/{id}")
    public ResponseEntity<RefundRequestResponse> get(@PathVariable Long id) { return ResponseEntity.ok(service.get(id)); }
    @PostMapping("/{id}/approve")
    public ResponseEntity<RefundRequestResponse> approve(@PathVariable Long id) { return ResponseEntity.ok(service.approve(id)); }
    @PostMapping("/{id}/reject")
    public ResponseEntity<RefundRequestResponse> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.reject(id, body.get("rejectReason")));
    }
}
