package com.hotel.billing.service;

import com.hotel.billing.dto.PaymentTransactionRequest;
import com.hotel.billing.dto.PaymentTransactionResponse;
import com.hotel.billing.entity.Folio;
import com.hotel.billing.entity.PaymentTransaction;
import com.hotel.billing.entity.TransactionType;
import com.hotel.billing.exception.ResourceNotFoundException;
import com.hotel.billing.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
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

    @Transactional
    public PaymentTransactionResponse create(PaymentTransactionRequest request) {
        Folio folio = folioService.findEntity(request.getFolioId());

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
