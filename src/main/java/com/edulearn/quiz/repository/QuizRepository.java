package com.edulearn.quiz.repository;

import com.edulearn.quiz.entity.Quiz;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByLessonId(Long lessonId);
}

