package com.edulearn.course.repository;

import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @EntityGraph(attributePaths = {"categories"})
    Optional<Course> findBySlugAndStatus(String slug, CourseStatus status);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.categories WHERE c.status = :status ORDER BY c.publishedAt DESC")
    List<Course> findByStatusOrderByPublishedAtDesc(@Param("status") CourseStatus status);

    long countByStatus(CourseStatus status);

    @EntityGraph(attributePaths = {"categories"})
    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);
}

