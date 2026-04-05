package com.edulearn.course.dto;

import com.edulearn.course.entity.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdateRequest {

    @Size(max = 255, message = "Course title is too long")
    private String title;

    @Size(max = 255, message = "Course slug is too long")
    private String slug;

    private String description;

    @Size(max = 500, message = "Thumbnail URL is too long")
    private String thumbnailUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private BigDecimal price;

    private CourseLevel level;

    @Size(max = 10, message = "Language is too long")
    private String language;

    @Min(value = 0, message = "Duration hours cannot be negative")
    private Integer durationHours;

    private Set<Integer> categoryIds;
}

