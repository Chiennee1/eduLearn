package com.edulearn.quiz.repository;

import com.edulearn.quiz.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

	boolean existsByQuestion_Id(Long questionId);

	boolean existsBySelectedOption_Id(Long optionId);
}

