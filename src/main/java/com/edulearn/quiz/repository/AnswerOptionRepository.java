package com.edulearn.quiz.repository;

import com.edulearn.quiz.entity.AnswerOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    List<AnswerOption> findByQuestionIdOrderByOrderIndexAscIdAsc(Long questionId);

    long countByQuestionId(Long questionId);
}

