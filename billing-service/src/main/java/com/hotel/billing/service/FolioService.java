package com.hotel.billing.service;

import com.hotel.billing.client.BookingServiceClient;
import com.hotel.billing.dto.FolioItemResponse;
import com.hotel.billing.dto.FolioRequest;
import com.hotel.billing.dto.FolioResponse;
import com.hotel.billing.dto.FolioStatementResponse;
import com.hotel.billing.dto.PaymentTransactionResponse;
import com.hotel.billing.entity.Folio;
import com.hotel.billing.exception.DuplicateResourceException;
import com.hotel.billing.exception.ResourceNotFoundException;
import com.hotel.billing.repository.FolioItemRepository;
import com.hotel.billing.repository.FolioRepository;
import com.hotel.billing.repository.PaymentTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FolioService {

    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BookingServiceClient bookingServiceClient;

    public FolioService(FolioRepository folioRepository, FolioItemRepository folioItemRepository,
                         PaymentTransactionRepository paymentTransactionRepository,
                         BookingServiceClient bookingServiceClient) {
        this.folioRepository = folioRepository;
        this.folioItemRepository = folioItemRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bookingServiceClient = bookingServiceClient;
    }

    public Page<FolioResponse> getAll(Pageable pageable) {
        return folioRepository.findAll(pageable).map(FolioResponse::new);
    }

    public Page<FolioResponse> getUnpaid(Pageable pageable) {
        return folioRepository.findByBalanceGreaterThan(BigDecimal.ZERO, pageable).map(FolioResponse::new);
    }

    public FolioResponse getById(Long id) {
        return new FolioResponse(findEntity(id));
    }

    public FolioStatementResponse getStatement(Long id) {
        Folio folio = findEntity(id);
        var items = folioItemRepository.findByFolioId(id).stream().map(FolioItemResponse::new).toList();
        var payments = paymentTransactionRepository.findByFolioId(id).stream().map(PaymentTransactionResponse::new).toList();
        return new FolioStatementResponse(new FolioResponse(folio), items, payments);
    }

    public FolioResponse create(FolioRequest request) {
        if (folioRepository.existsByReservationId(request.getReservationId())) {
            throw new DuplicateResourceException("A folio already exists for reservation id: " + request.getReservationId());
        }
        // Validates the reservation really exists in booking-service before opening a folio.
        bookingServiceClient.getReservation(request.getReservationId());

        Folio folio = new Folio();
        folio.setReservationId(request.getReservationId());
        folio.setBalance(BigDecimal.ZERO);
        return new FolioResponse(folioRepository.save(folio));
    }

    public void delete(Long id) {
        folioRepository.delete(findEntity(id));
    }

    Folio findEntity(Long id) {
        return folioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folio not found with id: " + id));
    }

    void adjustBalance(Folio folio, BigDecimal delta) {
        folio.setBalance(folio.getBalance().add(delta));
        folioRepository.save(folio);
    }
}
