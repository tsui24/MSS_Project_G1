package com.hotel.billing.service;

import com.hotel.billing.dto.PaymentTransactionRequest;
import com.hotel.billing.dto.PaymentTransactionResponse;
import com.hotel.billing.entity.Folio;
import com.hotel.billing.entity.PaymentTransaction;
import com.hotel.billing.entity.TransactionType;
import com.hotel.billing.exception.ResourceNotFoundException;
import com.hotel.billing.exception.InvalidStateException;
import com.hotel.billing.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.hotel.billing.entity.PaymentMethod;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final FolioService folioService;

    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository, FolioService folioService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.folioService = folioService;
    }

    public List<PaymentTransactionResponse> getByFolioId(Long folioId) {
        return paymentTransactionRepository.findByFolioId(folioId).stream()
                .map(PaymentTransactionResponse::new).toList();
    }

    public Page<PaymentTransactionResponse> search(Long folioId, PaymentMethod paymentMethod,
                                                    TransactionType transactionType, String code,
                                                    String status, Pageable pageable) {
        Specification<PaymentTransaction> specification = Specification.where(null);
        if (folioId != null) specification = specification.and((root, query, cb) -> cb.equal(root.get("folio").get("id"), folioId));
        if (paymentMethod != null) specification = specification.and((root, query, cb) -> cb.equal(root.get("paymentMethod"), paymentMethod));
        if (transactionType != null) specification = specification.and((root, query, cb) -> cb.equal(root.get("transactionType"), transactionType));
        if (status != null && !status.isBlank() && !"SUCCESS".equalsIgnoreCase(status)) {
            specification = specification.and((root, query, cb) -> cb.disjunction());
        }
        if (code != null && !code.isBlank()) {
            String numeric = code.toUpperCase().replace("PAY-", "").trim();
            try {
                Long id = Long.valueOf(numeric);
                specification = specification.and((root, query, cb) -> cb.equal(root.get("id"), id));
            } catch (NumberFormatException ignored) {
                specification = specification.and((root, query, cb) -> cb.disjunction());
            }
        }
        return paymentTransactionRepository.findAll(specification, pageable).map(PaymentTransactionResponse::new);
    }

    public PaymentTransactionResponse getById(Long id) {
        return paymentTransactionRepository.findById(id).map(PaymentTransactionResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
    }

    @Transactional
    public PaymentTransactionResponse create(PaymentTransactionRequest request) {
        Folio folio = folioService.findEntity(request.getFolioId());
        if (request.getTransactionType() != TransactionType.REFUND
                && request.getAmount().compareTo(folio.getBalance()) > 0) {
            throw new InvalidStateException("Payment cannot exceed outstanding balance: " + folio.getBalance());
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setFolio(folio);
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTransactionType(request.getTransactionType());
        PaymentTransaction saved = paymentTransactionRepository.save(transaction);

        // Deposits/final payments reduce what's owed; a refund gives money back and increases the balance again.
        boolean isRefund = request.getTransactionType() == TransactionType.REFUND;
        folioService.adjustBalance(folio, isRefund ? request.getAmount() : request.getAmount().negate());

        return new PaymentTransactionResponse(saved);
    }

    public void delete(Long id) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
        paymentTransactionRepository.delete(transaction);

        boolean isRefund = transaction.getTransactionType() == TransactionType.REFUND;
        folioService.adjustBalance(transaction.getFolio(), isRefund ? transaction.getAmount().negate() : transaction.getAmount());
    }
}
