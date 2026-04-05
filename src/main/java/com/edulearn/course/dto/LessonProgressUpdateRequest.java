package com.edulearn.course.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonProgressUpdateRequest {

    @Min(value = 0, message = "Watched seconds cannot be negative")
    private Integer watchedSeconds;

    private Boolean completed;
}

