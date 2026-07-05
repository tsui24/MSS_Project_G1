package com.hotel.review.dto;

import com.hotel.review.entity.Review;

import java.time.Instant;

public class ReviewResponse {

    private Long id;
    private Long reservationId;
    private Long roomId;
    private Long customerId;
    private Integer rating;
    private String comment;
    private Instant createdAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.reservationId = review.getReservationId();
        this.roomId = review.getRoomId();
        this.customerId = review.getCustomerId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
