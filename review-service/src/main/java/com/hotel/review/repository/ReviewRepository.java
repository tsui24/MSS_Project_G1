package com.hotel.review.repository;

import com.hotel.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByRoomId(Long roomId, Pageable pageable);

    Page<Review> findByReservationId(Long reservationId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.roomId = :roomId")
    Double averageRatingForRoom(Long roomId);
}
