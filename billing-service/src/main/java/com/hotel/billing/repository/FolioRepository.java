package com.hotel.billing.repository;

import com.hotel.billing.entity.Folio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface FolioRepository extends JpaRepository<Folio, Long> {
    boolean existsByReservationId(Long reservationId);
    Optional<Folio> findByReservationId(Long reservationId);
    Page<Folio> findByBalanceGreaterThan(BigDecimal balance, Pageable pageable);
}
