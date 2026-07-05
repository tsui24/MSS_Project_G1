package com.hotel.billing.service;

import com.hotel.billing.dto.FolioItemRequest;
import com.hotel.billing.dto.FolioItemResponse;
import com.hotel.billing.entity.Folio;
import com.hotel.billing.entity.FolioItem;
import com.hotel.billing.exception.ResourceNotFoundException;
import com.hotel.billing.repository.FolioItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FolioItemService {

    private final FolioItemRepository folioItemRepository;
    private final FolioService folioService;

    public FolioItemService(FolioItemRepository folioItemRepository, FolioService folioService) {
        this.folioItemRepository = folioItemRepository;
        this.folioService = folioService;
    }

    public List<FolioItemResponse> getByFolioId(Long folioId) {
        return folioItemRepository.findByFolioId(folioId).stream().map(FolioItemResponse::new).toList();
    }

    @Transactional
    public FolioItemResponse create(FolioItemRequest request) {
        Folio folio = folioService.findEntity(request.getFolioId());

        FolioItem item = new FolioItem();
        item.setFolio(folio);
        item.setItemType(request.getItemType());
        item.setAmount(request.getAmount());
        FolioItem saved = folioItemRepository.save(item);

        // A charge item increases what the guest owes.
        folioService.adjustBalance(folio, request.getAmount());

        return new FolioItemResponse(saved);
    }

    public void delete(Long id) {
        FolioItem item = folioItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folio item not found with id: " + id));
        folioItemRepository.delete(item);
        folioService.adjustBalance(item.getFolio(), item.getAmount().negate());
    }
}
