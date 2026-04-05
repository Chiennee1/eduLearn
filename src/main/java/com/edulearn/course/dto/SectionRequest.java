package com.edulearn.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {

    @NotBlank(message = "Section title is required")
    @Size(max = 255, message = "Section title is too long")
    private String title;

    @Min(value = 0, message = "Order index cannot be negative")
    private Integer orderIndex;
}

