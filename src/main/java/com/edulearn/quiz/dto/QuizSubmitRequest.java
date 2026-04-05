package com.edulearn.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmitRequest {

    @Valid
    @NotEmpty(message = "Answers are required")
    private List<QuizSubmitAnswerRequest> answers = new ArrayList<>();
}

