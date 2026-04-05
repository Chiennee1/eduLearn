package com.edulearn.course.repository;

import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.entity.EnrollmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Enrollment> findByIdAndUserId(Long id, Long userId);

    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, EnrollmentStatus status);

    long countByCourseId(Long courseId);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
}

