package com.edulearn.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizCreateRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    @Min(value = 1, message = "Pass score must be >= 1")
    @Max(value = 100, message = "Pass score must be <= 100")
    private Integer passScore;

    @Min(value = 1, message = "Time limit must be >= 1 minute")
    private Integer timeLimitMins;

    @Valid
    @NotEmpty(message = "Quiz must have at least one question")
    private List<QuizQuestionRequest> questions = new ArrayList<>();
}

