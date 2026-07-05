package com.hotel.review.controller;

import com.hotel.review.dto.ReviewRequest;
import com.hotel.review.dto.ReviewResponse;
import com.hotel.review.dto.RoomAverageRatingResponse;
import com.hotel.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Post-stay guest reviews and room ratings")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "List reviews for a room or a reservation (pass exactly one filter)")
    public ResponseEntity<Page<ReviewResponse>> search(@RequestParam(required = false) Long roomId,
                                                        @RequestParam(required = false) Long reservationId,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        if (reservationId != null) {
            return ResponseEntity.ok(reviewService.getByReservationId(reservationId, pageable));
        }
        return ResponseEntity.ok(reviewService.getByRoomId(roomId, pageable));
    }

    @GetMapping("/room/{roomId}/average")
    @Operation(summary = "Average rating and review count for a room")
    public ResponseEntity<RoomAverageRatingResponse> averageForRoom(@PathVariable Long roomId) {
        double average = reviewService.averageRatingForRoom(roomId);
        long count = reviewService.reviewCountForRoom(roomId);
        return ResponseEntity.ok(new RoomAverageRatingResponse(roomId, average, count));
    }

    @PostMapping
    @Operation(summary = "Submit a review for a completed stay")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review's rating/comment")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
