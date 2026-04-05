package com.edulearn.course.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LessonProgressResponse {

    private final Long id;
    private final Long lessonId;
    private final String lessonTitle;
    private final boolean completed;
    private final Integer watchedSeconds;
    private final LocalDateTime lastAccessed;
}

