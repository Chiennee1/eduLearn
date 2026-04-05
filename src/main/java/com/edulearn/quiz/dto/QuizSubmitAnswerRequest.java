package com.edulearn.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmitAnswerRequest {

    @NotNull(message = "Question id is required")
    private Long questionId;

    private Long selectedOptionId;
}

