package com.edulearn.course.service;

import com.edulearn.course.dto.LessonProgressResponse;
import com.edulearn.course.dto.LessonProgressUpdateRequest;
import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.entity.EnrollmentStatus;
import com.edulearn.course.entity.LessonProgress;
import com.edulearn.course.repository.LessonProgressRepository;
import com.edulearn.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LearningProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentService enrollmentService;
    private final CertificateService certificateService;

    @Transactional(readOnly = true)
    public List<LessonProgressResponse> getMyLessonProgress(Long enrollmentId, String actorEmail) {
        Enrollment enrollment = enrollmentService.getMyEnrollmentEntity(enrollmentId, actorEmail);
        return lessonProgressRepository.findByEnrollmentIdOrderByLessonSectionOrderIndexAscLessonOrderIndexAsc(enrollment.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LessonProgressResponse updateMyLessonProgress(
            Long enrollmentId,
            Long lessonId,
            LessonProgressUpdateRequest request,
            String actorEmail
    ) {
        Enrollment enrollment = enrollmentService.getMyEnrollmentEntity(enrollmentId, actorEmail);

        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson progress not found"));

        if (request.getWatchedSeconds() != null) {
            progress.setWatchedSeconds(request.getWatchedSeconds());
        }
        if (request.getCompleted() != null) {
            progress.setCompleted(request.getCompleted());
        }
        progress.setLastAccessed(LocalDateTime.now());

        progress = lessonProgressRepository.save(progress);

        enrollmentService.markCompletedIfEligible(enrollment);
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            certificateService.issueIfMissing(enrollment);
        }

        return toResponse(progress);
    }

    private LessonProgressResponse toResponse(LessonProgress progress) {
        return LessonProgressResponse.builder()
                .id(progress.getId())
                .lessonId(progress.getLesson().getId())
                .lessonTitle(progress.getLesson().getTitle())
                .completed(progress.isCompleted())
                .watchedSeconds(progress.getWatchedSeconds())
                .lastAccessed(progress.getLastAccessed())
                .build();
    }
}

