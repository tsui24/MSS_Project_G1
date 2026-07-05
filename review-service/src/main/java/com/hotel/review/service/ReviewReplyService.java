package com.hotel.review.service;

import com.hotel.review.dto.ReviewReplyRequest;
import com.hotel.review.dto.ReviewReplyResponse;
import com.hotel.review.entity.Review;
import com.hotel.review.entity.ReviewReply;
import com.hotel.review.exception.ResourceNotFoundException;
import com.hotel.review.repository.ReviewReplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewService reviewService;

    public ReviewReplyService(ReviewReplyRepository reviewReplyRepository, ReviewService reviewService) {
        this.reviewReplyRepository = reviewReplyRepository;
        this.reviewService = reviewService;
    }

    public List<ReviewReplyResponse> getByReviewId(Long reviewId) {
        return reviewReplyRepository.findByReviewId(reviewId).stream().map(ReviewReplyResponse::new).toList();
    }

    public ReviewReplyResponse create(Long reviewId, ReviewReplyRequest request) {
        Review review = reviewService.findEntity(reviewId);

        ReviewReply reply = new ReviewReply();
        reply.setReview(review);
        reply.setStaffId(request.getStaffId());
        reply.setReplyMessage(request.getReplyMessage());
        return new ReviewReplyResponse(reviewReplyRepository.save(reply));
    }

    public void delete(Long id) {
        ReviewReply reply = reviewReplyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review reply not found with id: " + id));
        reviewReplyRepository.delete(reply);
    }
}
