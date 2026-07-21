package com.hotel.billing.repository;

import com.hotel.billing.entity.RefundRequest;
import com.hotel.billing.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    Page<RefundRequest> findByStatusOrderByCreatedAtDesc(RefundStatus status, Pageable pageable);
}
