package com.hotel.billing.controller;

import com.hotel.billing.dto.FolioItemRequest;
import com.hotel.billing.dto.FolioItemResponse;
import com.hotel.billing.service.FolioItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing/folio-items")
@Tag(name = "Folio Items", description = "Individual charges posted to a folio (ROOM_CHARGE, MINIBAR, SERVICE...)")
public class FolioItemController {

    private final FolioItemService folioItemService;

    public FolioItemController(FolioItemService folioItemService) {
        this.folioItemService = folioItemService;
    }

    @GetMapping
    @Operation(summary = "List charge items for a folio")
    public ResponseEntity<List<FolioItemResponse>> getByFolio(@RequestParam Long folioId) {
        return ResponseEntity.ok(folioItemService.getByFolioId(folioId));
    }

    @PostMapping
    @Operation(summary = "Post a charge to a folio (increases the folio balance)")
    public ResponseEntity<FolioItemResponse> create(@Valid @RequestBody FolioItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(folioItemService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Void a charge item (decreases the folio balance back)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        folioItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
