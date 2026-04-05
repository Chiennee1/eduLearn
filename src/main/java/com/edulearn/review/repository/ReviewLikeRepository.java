package com.edulearn.review.repository;

import com.edulearn.review.entity.ReviewLike;
import com.edulearn.review.entity.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    long countByReview_Id(Long reviewId);

    boolean existsByReview_IdAndUser_Id(Long reviewId, Long userId);
}


