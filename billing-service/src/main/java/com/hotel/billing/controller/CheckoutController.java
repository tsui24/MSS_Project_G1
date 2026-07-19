package com.hotel.billing.controller;

import com.hotel.billing.dto.CheckoutSummaryResponse;
import com.hotel.billing.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/checkout")
public class CheckoutController {
    private final CheckoutService service;
    public CheckoutController(CheckoutService service){this.service=service;}
    @GetMapping("/{reservationId}")
    public ResponseEntity<CheckoutSummaryResponse> summary(@PathVariable Long reservationId){
        return ResponseEntity.ok(service.prepare(reservationId));
    }
    @PostMapping("/{reservationId}/finalize")
    public ResponseEntity<CheckoutSummaryResponse> finalizeCheckout(@PathVariable Long reservationId){
        return ResponseEntity.ok(service.finalizeCheckout(reservationId));
    }
}
