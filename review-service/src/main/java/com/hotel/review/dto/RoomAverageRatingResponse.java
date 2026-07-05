package com.hotel.review.dto;

public class RoomAverageRatingResponse {

    private Long roomId;
    private Double averageRating;
    private long reviewCount;

    public RoomAverageRatingResponse(Long roomId, Double averageRating, long reviewCount) {
        this.roomId = roomId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public long getReviewCount() {
        return reviewCount;
    }
}
