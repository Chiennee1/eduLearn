package com.edulearn.course.service;

import com.edulearn.auth.entity.User;
import com.edulearn.course.dto.EnrollmentResponse;
import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.entity.EnrollmentStatus;
import com.edulearn.course.entity.Lesson;
import com.edulearn.course.entity.LessonProgress;
import com.edulearn.course.event.CourseEnrolledEvent;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.course.repository.LessonProgressRepository;
import com.edulearn.course.repository.LessonRepository;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseService courseService;
    private final LearningAccessService learningAccessService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public EnrollmentResponse enroll(Long courseId, String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        learningAccessService.requireStudent(actor);

        Course course = courseService.getEntity(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course not found");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(actor.getId(), courseId)) {
            throw new BusinessException("You are already enrolled in this course", HttpStatus.CONFLICT);
        }

        List<Lesson> lessons = lessonRepository.findByCourseIdOrdered(courseId);
        if (lessons.isEmpty()) {
            throw new BusinessException("Course has no learning content", HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = Enrollment.builder()
                .user(actor)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .amountPaid(course.getPrice())
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        List<LessonProgress> progresses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            progresses.add(LessonProgress.builder()
                    .enrollment(enrollment)
                    .lesson(lesson)
                    .completed(false)
                    .watchedSeconds(0)
                    .build());
        }
        lessonProgressRepository.saveAll(progresses);

        eventPublisher.publishEvent(new CourseEnrolledEvent(
                this,
                enrollment.getId(),
                actor.getEmail(),
                course.getTitle(),
                enrollment.getAmountPaid()
        ));

        return toResponse(enrollment, 0L, (long) lessons.size());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        learningAccessService.requireStudent(actor);

        return enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(actor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void markCompletedIfEligible(Enrollment enrollment) {
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            return;
        }

        long total = lessonProgressRepository.countByEnrollmentId(enrollment.getId());
        long completed = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());
        if (total > 0 && completed == total) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }

    @Transactional(readOnly = true)
    public Enrollment getMyEnrollmentEntity(Long enrollmentId, String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        learningAccessService.requireStudent(actor);
        return enrollmentRepository.findByIdAndUserId(enrollmentId, actor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse toResponse(Enrollment enrollment) {
        long total = lessonProgressRepository.countByEnrollmentId(enrollment.getId());
        long completed = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());
        return toResponse(enrollment, completed, total);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment, long completedLessons, long totalLessons) {
        int progressPercent = totalLessons == 0 ? 0 : (int) Math.round((completedLessons * 100.0) / totalLessons);
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .status(enrollment.getStatus())
                .amountPaid(enrollment.getAmountPaid())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .completedLessons(completedLessons)
                .totalLessons(totalLessons)
                .progressPercent(progressPercent)
                .build();
    }
}

