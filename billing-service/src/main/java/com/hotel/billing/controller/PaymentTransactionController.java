package com.hotel.billing.controller;

import com.hotel.billing.dto.PaymentTransactionRequest;
import com.hotel.billing.dto.PaymentTransactionResponse;
import com.hotel.billing.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing/payments")
@Tag(name = "Payment Transactions", description = "Cash/card deposits, final payments and refunds against a folio")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping
    @Operation(summary = "List payment transactions for a folio")
    public ResponseEntity<List<PaymentTransactionResponse>> getByFolio(@RequestParam Long folioId) {
        return ResponseEntity.ok(paymentTransactionService.getByFolioId(folioId));
    }

    @PostMapping
    @Operation(summary = "Record a payment (deposit/final payment reduces balance, refund increases it)")
    public ResponseEntity<PaymentTransactionResponse> create(@Valid @RequestBody PaymentTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentTransactionService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Void a payment transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
