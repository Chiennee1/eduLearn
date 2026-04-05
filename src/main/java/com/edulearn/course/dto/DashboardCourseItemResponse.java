package com.edulearn.course.dto;

import com.edulearn.course.entity.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardCourseItemResponse {

    private final Long enrollmentId;
    private final Long courseId;
    private final String courseTitle;
    private final EnrollmentStatus status;
    private final int progressPercent;
    private final String certificateCode;
}

