package com.edulearn.admin.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardStatsResponse {

    private final long totalUsers;
    private final long totalStudents;
    private final long totalInstructors;
    private final long totalCourses;
    private final long publishedCourses;
    private final long totalEnrollments;
    private final long completedEnrollments;
    private final long pendingOrders;
    private final long completedOrders;
    private final long totalReviews;
    private final BigDecimal totalRevenue;
}

