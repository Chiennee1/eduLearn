package com.edulearn.course.dto;

import com.edulearn.course.entity.LessonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    @Size(max = 255, message = "Lesson title is too long")
    private String title;

    @NotNull(message = "Lesson type is required")
    private LessonType type;

    @Size(max = 500, message = "Content URL is too long")
    private String contentUrl;

    @Min(value = 0, message = "Duration seconds cannot be negative")
    private Integer durationSeconds;

    private Boolean preview;

    @Min(value = 0, message = "Order index cannot be negative")
    private Integer orderIndex;
}

