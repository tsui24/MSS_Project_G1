package com.hotel.billing.controller;

import com.hotel.billing.dto.FolioRequest;
import com.hotel.billing.dto.FolioResponse;
import com.hotel.billing.dto.FolioStatementResponse;
import com.hotel.billing.service.FolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/folios")
@Tag(name = "Folios", description = "Per-reservation wallet tracking the outstanding balance")
public class FolioController {

    private final FolioService folioService;

    public FolioController(FolioService folioService) {
        this.folioService = folioService;
    }

    @GetMapping
    @Operation(summary = "List all folios, paginated")
    public ResponseEntity<Page<FolioResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(folioService.getAll(pageable));
    }

    @GetMapping("/unpaid")
    @Operation(summary = "List folios with an outstanding balance (balance > 0), paginated")
    public ResponseEntity<Page<FolioResponse>> getUnpaid(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(folioService.getUnpaid(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a folio by id")
    public ResponseEntity<FolioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(folioService.getById(id));
    }

    @GetMapping("/{id}/statement")
    @Operation(summary = "Full statement for a folio: balance plus all charge items and payments")
    public ResponseEntity<FolioStatementResponse> getStatement(@PathVariable Long id) {
        return ResponseEntity.ok(folioService.getStatement(id));
    }

    @PostMapping
    @Operation(summary = "Open a folio for a reservation (validates the reservation exists in booking-service)")
    public ResponseEntity<FolioResponse> create(@Valid @RequestBody FolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(folioService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a folio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        folioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
