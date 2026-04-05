package com.edulearn.review.repository;

import com.edulearn.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Review> findByUserIdAndCourseId(Long userId, Long courseId);

    List<Review> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    @Query("""
            select count(r), coalesce(avg(r.rating), 0)
            from Review r
            where r.course.id = :courseId
            """)
    List<Object[]> aggregateRatingByCourseId(@Param("courseId") Long courseId);
}


