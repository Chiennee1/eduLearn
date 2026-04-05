package com.edulearn.course.service;

import com.edulearn.auth.entity.User;
import com.edulearn.course.dto.DashboardCourseItemResponse;
import com.edulearn.course.dto.EnrollmentResponse;
import com.edulearn.course.dto.LearningDashboardResponse;
import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.entity.EnrollmentStatus;
import com.edulearn.course.repository.CourseCertificateRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LearningDashboardFacade {

    private final LearningAccessService learningAccessService;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    private final CourseCertificateRepository courseCertificateRepository;

    @Transactional(readOnly = true)
    public LearningDashboardResponse getStudentDashboard(String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        learningAccessService.requireStudent(actor);

        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(actor.getId());
        List<DashboardCourseItemResponse> items = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            EnrollmentResponse enrollmentResponse = enrollmentService.toResponse(enrollment);
            String certificateCode = courseCertificateRepository.findByEnrollmentId(enrollment.getId())
                    .map(certificate -> certificate.getCertificateCode())
                    .orElse(null);

            items.add(DashboardCourseItemResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .courseId(enrollment.getCourse().getId())
                    .courseTitle(enrollment.getCourse().getTitle())
                    .status(enrollment.getStatus())
                    .progressPercent(enrollmentResponse.getProgressPercent())
                    .certificateCode(certificateCode)
                    .build());
        }

        long total = enrollmentRepository.countByUserId(actor.getId());
        long active = enrollmentRepository.countByUserIdAndStatus(actor.getId(), EnrollmentStatus.ACTIVE);
        long completed = enrollmentRepository.countByUserIdAndStatus(actor.getId(), EnrollmentStatus.COMPLETED);

        return LearningDashboardResponse.builder()
                .totalEnrollments(total)
                .activeEnrollments(active)
                .completedEnrollments(completed)
                .courses(items)
                .build();
    }
}

