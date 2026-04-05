package com.edulearn.course.dto;

import com.edulearn.course.entity.LessonType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LessonResponse {

    private final Long id;
    private final Long sectionId;
    private final String title;
    private final LessonType type;
    private final String contentUrl;
    private final Integer durationSeconds;
    private final boolean preview;
    private final Integer orderIndex;
    private final LocalDateTime createdAt;
}

