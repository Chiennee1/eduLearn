package com.edulearn.course.repository;

import com.edulearn.course.entity.Lesson;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByOrderIndexAscIdAsc(Long sectionId);

    long countBySectionId(Long sectionId);
}

