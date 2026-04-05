package com.edulearn.review.service;

import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.EnrollmentStatus;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.exception.ResourceNotFoundException;
import com.edulearn.review.entity.CourseStats;
import com.edulearn.review.repository.CourseStatsRepository;
import com.edulearn.review.repository.ReviewRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseStatsService {

    private final CourseStatsRepository courseStatsRepository;
    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void recomputeCourseStats(Long courseId) {
        Object[] aggregate = reviewRepository.aggregateRatingByCourseId(courseId)
                .stream()
                .findFirst()
                .orElse(new Object[]{0L, BigDecimal.ZERO});

        long totalReviews = ((Number) aggregate[0]).longValue();
        BigDecimal avgRating = new BigDecimal(String.valueOf(aggregate[1])).setScale(2, RoundingMode.HALF_UP);

        long totalEnrollments = enrollmentRepository.countByCourseId(courseId);
        long totalCompletions = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.COMPLETED);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseStats stats = courseStatsRepository.findByCourseId(courseId)
                .orElseGet(() -> CourseStats.builder().course(course).build());
        stats.setTotalReviews((int) totalReviews);
        stats.setAvgRating(avgRating);
        stats.setTotalEnrollments((int) totalEnrollments);
        stats.setTotalCompletions((int) totalCompletions);

        courseStatsRepository.save(stats);
    }
}

