package com.hotel.review.service;

import com.hotel.review.dto.ReviewRequest;
import com.hotel.review.dto.ReviewResponse;
import com.hotel.review.entity.Review;
import com.hotel.review.exception.ResourceNotFoundException;
import com.hotel.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Page<ReviewResponse> getByRoomId(Long roomId, Pageable pageable) {
        if (roomId == null) {
            return reviewRepository.findAll(pageable).map(ReviewResponse::new);
        }
        return reviewRepository.findByRoomId(roomId, pageable).map(ReviewResponse::new);
    }

    public Page<ReviewResponse> getByReservationId(Long reservationId, Pageable pageable) {
        return reviewRepository.findByReservationId(reservationId, pageable).map(ReviewResponse::new);
    }

    public double averageRatingForRoom(Long roomId) {
        Double average = reviewRepository.averageRatingForRoom(roomId);
        return average != null ? average : 0.0;
    }

    public long reviewCountForRoom(Long roomId) {
        return reviewRepository.findByRoomId(roomId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
    }

    public ReviewResponse create(ReviewRequest request) {
        Review review = new Review();
        review.setReservationId(request.getReservationId());
        review.setRoomId(request.getRoomId());
        review.setCustomerId(request.getCustomerId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return new ReviewResponse(reviewRepository.save(review));
    }

    public ReviewResponse update(Long id, ReviewRequest request) {
        Review review = findEntity(id);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return new ReviewResponse(reviewRepository.save(review));
    }

    public void delete(Long id) {
        reviewRepository.delete(findEntity(id));
    }

    Review findEntity(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }
}
