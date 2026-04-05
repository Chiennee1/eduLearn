package com.edulearn.course.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LearningDashboardResponse {

    private final long totalEnrollments;
    private final long activeEnrollments;
    private final long completedEnrollments;
    private final List<DashboardCourseItemResponse> courses;
}

