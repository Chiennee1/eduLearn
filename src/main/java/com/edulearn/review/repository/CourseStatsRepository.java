package com.edulearn.review.repository;

import com.edulearn.review.entity.CourseStats;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseStatsRepository extends JpaRepository<CourseStats, Long> {

    Optional<CourseStats> findByCourseId(Long courseId);
}

