package com.hotel.review.entity;

import jakarta.persistence.*;

import java.time.Instant;

/** Staff response to a guest review. Lives in the same DB as Review, so this is a real foreign key. */
@Entity
@Table(name = "review_replies")
public class ReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /** Logical reference to auth-service users(id) of the staff member who replied; not a DB foreign key. */
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "reply_message", nullable = false, length = 1000)
    private String replyMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
