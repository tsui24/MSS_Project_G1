package com.hotel.billing.repository;

import com.hotel.billing.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long>, JpaSpecificationExecutor<PaymentTransaction> {
    List<PaymentTransaction> findByFolioId(Long folioId);

    @Query("SELECT COALESCE(SUM(CASE WHEN p.transactionType = 'REFUND' THEN -p.amount ELSE p.amount END), 0) " +
            "FROM PaymentTransaction p WHERE p.createdAt >= :from AND p.createdAt < :to")
    java.math.BigDecimal sumNetRevenue(@Param("from") Instant from, @Param("to") Instant to);
}
