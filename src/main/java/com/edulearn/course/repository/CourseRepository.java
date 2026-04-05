package com.edulearn.course.repository;

import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Course> findByStatusOrderByPublishedAtDesc(CourseStatus status);

    long countByStatus(CourseStatus status);

    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);
}

