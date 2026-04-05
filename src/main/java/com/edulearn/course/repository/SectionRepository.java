package com.edulearn.course.repository;

import com.edulearn.course.entity.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByCourseIdOrderByOrderIndexAscIdAsc(Long courseId);

    long countByCourseId(Long courseId);

    @Query("""
            select s.id
            from Section s
            where s.course.id = :courseId
              and not exists (select l.id from Lesson l where l.section = s)
            """)
    List<Long> findSectionIdsWithoutLessons(@Param("courseId") Long courseId);
}

