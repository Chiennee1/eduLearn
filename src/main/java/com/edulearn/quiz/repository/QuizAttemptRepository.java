package com.edulearn.quiz.repository;

import com.edulearn.quiz.entity.QuizAttempt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long>, JpaSpecificationExecutor<QuizAttempt> {

    Optional<QuizAttempt> findByIdAndUserId(Long id, Long userId);
}

