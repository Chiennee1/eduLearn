package com.edulearn.quiz.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResponse {

    private final Long id;
    private final Long lessonId;
    private final String title;
    private final Integer passScore;
    private final Integer timeLimitMins;
    private final List<QuizQuestionResponse> questions;
}

