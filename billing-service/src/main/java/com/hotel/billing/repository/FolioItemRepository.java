package com.hotel.billing.repository;

import com.hotel.billing.entity.FolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolioItemRepository extends JpaRepository<FolioItem, Long> {
    List<FolioItem> findByFolioId(Long folioId);
    Optional<FolioItem> findByFolioIdAndReferenceKey(Long folioId, String referenceKey);
}
