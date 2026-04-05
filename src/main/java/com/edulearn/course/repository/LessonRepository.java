package com.edulearn.course.repository;

import com.edulearn.course.entity.Lesson;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByOrderIndexAscIdAsc(Long sectionId);

    @Query("""
            select l
            from Lesson l
            where l.section.course.id = :courseId
            order by l.section.orderIndex asc, l.section.id asc, l.orderIndex asc, l.id asc
            """)
    List<Lesson> findByCourseIdOrdered(@Param("courseId") Long courseId);

    long countBySectionId(Long sectionId);
}

