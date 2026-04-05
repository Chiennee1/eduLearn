package com.edulearn.quiz.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizAttemptResponse {

    private final Long id;
    private final Long quizId;
    private final String quizTitle;
    private final Integer score;
    private final boolean passed;
    private final LocalDateTime startedAt;
    private final LocalDateTime submittedAt;
}

