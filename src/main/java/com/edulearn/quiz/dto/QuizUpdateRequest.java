package com.edulearn.quiz.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizUpdateRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    @Min(value = 1, message = "Pass score must be >= 1")
    @Max(value = 100, message = "Pass score must be <= 100")
    private Integer passScore;

    @Min(value = 1, message = "Time limit must be >= 1 minute")
    private Integer timeLimitMins;
}

