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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hotel.billing.entity.PaymentMethod;
import com.hotel.billing.entity.TransactionType;

@RestController
@RequestMapping("/api/billing/payments")
@Tag(name = "Payment Transactions", description = "Cash/card deposits, final payments and refunds against a folio")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping
    @Operation(summary = "Search payment transactions")
    public ResponseEntity<Page<PaymentTransactionResponse>> search(
            @RequestParam(name="folioId", required=false) Long folioId,
            @RequestParam(name="paymentMethod", required=false) PaymentMethod paymentMethod,
            @RequestParam(name="type", required=false) TransactionType transactionType,
            @RequestParam(name="code", required=false) String code,
            @RequestParam(name="status", required=false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(paymentTransactionService.search(folioId, paymentMethod, transactionType, code, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentTransactionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Record a payment (deposit/final payment reduces balance, refund increases it)")
    public ResponseEntity<PaymentTransactionResponse> create(@Valid @RequestBody PaymentTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentTransactionService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Void a payment transaction")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        paymentTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
