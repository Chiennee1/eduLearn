package com.edulearn.course.dto;

import com.edulearn.course.entity.CourseLevel;
import com.edulearn.course.entity.CourseStatus;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final Long instructorId;
    private final String title;
    private final String slug;
    private final String description;
    private final String thumbnailUrl;
    private final BigDecimal price;
    private final CourseLevel level;
    private final CourseStatus status;
    private final String language;
    private final Integer durationHours;
    private final LocalDateTime publishedAt;
    private final Set<Integer> categoryIds;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
