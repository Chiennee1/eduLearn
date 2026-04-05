package com.edulearn.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizOptionUpdateRequest {

    @NotBlank(message = "Option content is required")
    private String content;

    private Boolean correct;

    private Integer orderIndex;
}

