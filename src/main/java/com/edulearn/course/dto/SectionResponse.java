package com.edulearn.course.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionResponse {

    private final Long id;
    private final Long courseId;
    private final String title;
    private final Integer orderIndex;
    private final LocalDateTime createdAt;
}

