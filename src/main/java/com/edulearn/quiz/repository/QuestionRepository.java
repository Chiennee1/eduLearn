package com.edulearn.quiz.repository;

import com.edulearn.quiz.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizIdOrderByOrderIndexAscIdAsc(Long quizId);

    long countByQuizId(Long quizId);
}

