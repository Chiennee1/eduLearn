package com.edulearn.course.dto;

import com.edulearn.course.entity.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCreateRequest {

    @NotBlank(message = "Course title is required")
    @Size(max = 255, message = "Course title is too long")
    private String title;

    @Size(max = 255, message = "Course slug is too long")
    private String slug;

    private String description;

    @Size(max = 500, message = "Thumbnail URL is too long")
    private String thumbnailUrl;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    @NotBlank(message = "Language is required")
    @Size(max = 10, message = "Language is too long")
    private String language;

    @NotNull(message = "Duration hours is required")
    @Min(value = 0, message = "Duration hours cannot be negative")
    private Integer durationHours;

    private Long instructorId;

    private Set<Integer> categoryIds = new HashSet<>();
}

