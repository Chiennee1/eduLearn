package com.edulearn.quiz.dto;

import com.edulearn.quiz.entity.QuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizQuestionUpdateRequest {

    @NotBlank(message = "Question content is required")
    private String content;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    private Integer orderIndex;
}

