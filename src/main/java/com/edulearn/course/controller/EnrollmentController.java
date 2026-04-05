package com.edulearn.course.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.course.dto.CertificateResponse;
import com.edulearn.course.dto.EnrollmentResponse;
import com.edulearn.course.dto.LessonProgressResponse;
import com.edulearn.course.dto.LessonProgressUpdateRequest;
import com.edulearn.course.service.CertificateService;
import com.edulearn.course.service.EnrollmentService;
import com.edulearn.course.service.LearningProgressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_PREFIX)
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final LearningProgressService learningProgressService;
    private final CertificateService certificateService;

    @PostMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        EnrollmentResponse response = enrollmentService.enroll(courseId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Enrolled successfully", response));
    }

    @GetMapping("/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(Authentication authentication) {
        List<EnrollmentResponse> response = enrollmentService.getMyEnrollments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("My enrollments fetched", response));
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<LessonProgressResponse>>> getMyProgress(
            @PathVariable Long enrollmentId,
            Authentication authentication
    ) {
        List<LessonProgressResponse> response = learningProgressService.getMyLessonProgress(
                enrollmentId,
                authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Lesson progress fetched", response));
    }

    @PatchMapping("/enrollments/{enrollmentId}/lessons/{lessonId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> updateMyProgress(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonProgressUpdateRequest request,
            Authentication authentication
    ) {
        LessonProgressResponse response = learningProgressService.updateMyLessonProgress(
                enrollmentId,
                lessonId,
                request,
                authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Lesson progress updated", response));
    }

    @GetMapping("/enrollments/{enrollmentId}/certificate")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<CertificateResponse>> getMyCertificate(
            @PathVariable Long enrollmentId,
            Authentication authentication
    ) {
        // Ownership check is done by loading enrollment through EnrollmentService.
        enrollmentService.getMyEnrollmentEntity(enrollmentId, authentication.getName());
        CertificateResponse response = certificateService.getByEnrollmentId(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Certificate fetched", response));
    }
}
