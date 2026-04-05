package com.edulearn.quiz.dto;

import com.edulearn.quiz.entity.QuestionType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizQuestionResponse {

    private final Long id;
    private final String content;
    private final QuestionType type;
    private final Integer points;
    private final Integer orderIndex;
    private final List<QuizOptionResponse> options;
}

