package com.edulearn.quiz.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizOptionResponse {

    private final Long id;
    private final String content;
    private final Integer orderIndex;
}

