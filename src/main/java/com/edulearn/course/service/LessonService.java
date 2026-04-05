package com.edulearn.course.service;

import com.edulearn.auth.entity.User;
import com.edulearn.course.dto.LessonRequest;
import com.edulearn.course.dto.LessonResponse;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.entity.Lesson;
import com.edulearn.course.entity.Section;
import com.edulearn.course.repository.LessonRepository;
import com.edulearn.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SectionService sectionService;
    private final CoursePermissionService permissionService;

    @Transactional(readOnly = true)
    public List<LessonResponse> getPublicBySectionId(Long sectionId) {
        Section section = sectionService.getEntity(sectionId);
        if (section.getCourse().getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Section not found");
        }
        return lessonRepository.findBySectionIdOrderByOrderIndexAscIdAsc(sectionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LessonResponse getPublicById(Long lessonId) {
        Lesson lesson = getEntity(lessonId);
        if (lesson.getSection().getCourse().getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Lesson not found");
        }
        return toResponse(lesson);
    }

    @Transactional
    public LessonResponse create(Long sectionId, LessonRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Section section = sectionService.getEntity(sectionId);
        permissionService.requireCourseWriteAccess(actor, section.getCourse());

        Lesson lesson = Lesson.builder()
                .section(section)
                .title(request.getTitle().trim())
                .type(request.getType())
                .contentUrl(request.getContentUrl())
                .durationSeconds(request.getDurationSeconds() == null ? 0 : request.getDurationSeconds())
                .preview(request.getPreview() != null && request.getPreview())
                .orderIndex(request.getOrderIndex() == null ? 0 : request.getOrderIndex())
                .build();
        return toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse update(Long lessonId, LessonRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Lesson lesson = getEntity(lessonId);
        permissionService.requireCourseWriteAccess(actor, lesson.getSection().getCourse());

        lesson.setTitle(request.getTitle().trim());
        lesson.setType(request.getType());
        lesson.setContentUrl(request.getContentUrl());
        lesson.setDurationSeconds(request.getDurationSeconds() == null ? lesson.getDurationSeconds() : request.getDurationSeconds());
        lesson.setPreview(request.getPreview() != null ? request.getPreview() : lesson.isPreview());
        lesson.setOrderIndex(request.getOrderIndex() == null ? lesson.getOrderIndex() : request.getOrderIndex());
        return toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void delete(Long lessonId, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Lesson lesson = getEntity(lessonId);
        permissionService.requireCourseWriteAccess(actor, lesson.getSection().getCourse());
        lessonRepository.delete(lesson);
    }

    private Lesson getEntity(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }

    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .sectionId(lesson.getSection().getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .contentUrl(lesson.getContentUrl())
                .durationSeconds(lesson.getDurationSeconds())
                .preview(lesson.isPreview())
                .orderIndex(lesson.getOrderIndex())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}

