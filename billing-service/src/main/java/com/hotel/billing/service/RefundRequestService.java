package com.hotel.billing.service;

import com.hotel.billing.dto.PaymentTransactionRequest;
import com.hotel.billing.dto.RefundRequestResponse;
import com.hotel.billing.entity.*;
import com.hotel.billing.exception.InvalidStateException;
import com.hotel.billing.exception.ResourceNotFoundException;
import com.hotel.billing.repository.RefundRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class RefundRequestService {
    private final RefundRequestRepository repository;
    private final PaymentTransactionService paymentService;

    public RefundRequestService(RefundRequestRepository repository, PaymentTransactionService paymentService) {
        this.repository = repository;
        this.paymentService = paymentService;
    }

    public Page<RefundRequestResponse> pending(Pageable pageable) {
        return repository.findByStatusOrderByCreatedAtDesc(RefundStatus.PENDING, pageable)
                .map(RefundRequestResponse::new);
    }

    public RefundRequestResponse get(Long id) { return new RefundRequestResponse(find(id)); }

    @Transactional
    public RefundRequestResponse approve(Long id) {
        RefundRequest request = findPending(id);
        PaymentTransactionRequest transaction = new PaymentTransactionRequest();
        transaction.setFolioId(request.getPaymentTransaction().getFolio().getId());
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(request.getPaymentTransaction().getPaymentMethod());
        transaction.setTransactionType(TransactionType.REFUND);
        paymentService.create(transaction);
        request.setStatus(RefundStatus.APPROVED);
        request.setResolvedAt(Instant.now());
        return new RefundRequestResponse(repository.save(request));
    }

    @Transactional
    public RefundRequestResponse reject(Long id, String reason) {
        if (reason == null || reason.isBlank()) throw new InvalidStateException("Reject reason is required");
        RefundRequest request = findPending(id);
        request.setStatus(RefundStatus.REJECTED);
        request.setRejectReason(reason.trim());
        request.setResolvedAt(Instant.now());
        return new RefundRequestResponse(repository.save(request));
    }

    private RefundRequest find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Refund request not found with id: " + id));
    }
    private RefundRequest findPending(Long id) {
        RefundRequest request = find(id);
        if (request.getStatus() != RefundStatus.PENDING) throw new InvalidStateException("Refund request is already resolved");
        return request;
    }
}
