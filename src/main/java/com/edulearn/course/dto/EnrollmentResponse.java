package com.edulearn.course.dto;

import com.edulearn.course.entity.EnrollmentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrollmentResponse {

    private final Long id;
    private final Long courseId;
    private final String courseTitle;
    private final EnrollmentStatus status;
    private final BigDecimal amountPaid;
    private final LocalDateTime enrolledAt;
    private final LocalDateTime completedAt;
    private final long completedLessons;
    private final long totalLessons;
    private final int progressPercent;
}

