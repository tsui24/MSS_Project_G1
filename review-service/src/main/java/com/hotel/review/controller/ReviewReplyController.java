package com.hotel.review.controller;

import com.hotel.review.dto.ReviewReplyRequest;
import com.hotel.review.dto.ReviewReplyResponse;
import com.hotel.review.service.ReviewReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews/{reviewId}/replies")
@Tag(name = "Review Replies", description = "Staff responses to a guest review")
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    public ReviewReplyController(ReviewReplyService reviewReplyService) {
        this.reviewReplyService = reviewReplyService;
    }

    @GetMapping
    @Operation(summary = "List staff replies to a review")
    public ResponseEntity<List<ReviewReplyResponse>> getByReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewReplyService.getByReviewId(reviewId));
    }

    @PostMapping
    @Operation(summary = "Add a staff reply to a review")
    public ResponseEntity<ReviewReplyResponse> create(@PathVariable Long reviewId,
                                                       @Valid @RequestBody ReviewReplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewReplyService.create(reviewId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a staff reply")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @PathVariable Long id) {
        reviewReplyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
