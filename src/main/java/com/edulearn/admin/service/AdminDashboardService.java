package com.edulearn.admin.service;

import com.edulearn.admin.dto.AdminDashboardStatsResponse;
import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.entity.EnrollmentStatus;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.payment.entity.OrderStatus;
import com.edulearn.payment.repository.OrderRepository;
import com.edulearn.review.repository.ReviewRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getStats() {
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);

        return AdminDashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalStudents(userRepository.countByRoleName(RoleName.STUDENT))
                .totalInstructors(userRepository.countByRoleName(RoleName.INSTRUCTOR))
                .totalCourses(courseRepository.count())
                .publishedCourses(courseRepository.countByStatus(CourseStatus.PUBLISHED))
                .totalEnrollments(enrollmentRepository.count())
                .completedEnrollments(enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED))
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .completedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED))
                .totalReviews(reviewRepository.count())
                .totalRevenue(totalRevenue)
                .build();
    }
}

