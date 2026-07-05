package com.hotel.review.dto;

import com.hotel.review.entity.ReviewReply;

import java.time.Instant;

public class ReviewReplyResponse {

    private Long id;
    private Long reviewId;
    private Long staffId;
    private String replyMessage;
    private Instant createdAt;

    public ReviewReplyResponse(ReviewReply reply) {
        this.id = reply.getId();
        this.reviewId = reply.getReview().getId();
        this.staffId = reply.getStaffId();
        this.replyMessage = reply.getReplyMessage();
        this.createdAt = reply.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
