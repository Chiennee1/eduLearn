package com.edulearn.course.service;

import com.edulearn.auth.entity.User;
import com.edulearn.course.dto.SectionRequest;
import com.edulearn.course.dto.SectionResponse;
import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.entity.Section;
import com.edulearn.course.repository.SectionRepository;
import com.edulearn.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final CourseService courseService;
    private final SectionRepository sectionRepository;
    private final CoursePermissionService permissionService;

    @Transactional(readOnly = true)
    public List<SectionResponse> getPublicByCourseId(Long courseId) {
        Course course = courseService.getEntity(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course not found");
        }
        return sectionRepository.findByCourseIdOrderByOrderIndexAscIdAsc(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SectionResponse create(Long courseId, SectionRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Course course = courseService.getEntity(courseId);
        permissionService.requireCourseWriteAccess(actor, course);

        Section section = Section.builder()
                .course(course)
                .title(request.getTitle().trim())
                .orderIndex(request.getOrderIndex() == null ? 0 : request.getOrderIndex())
                .build();
        return toResponse(sectionRepository.save(section));
    }

    @Transactional
    public SectionResponse update(Long courseId, Long sectionId, SectionRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Section section = getInCourse(courseId, sectionId);
        permissionService.requireCourseWriteAccess(actor, section.getCourse());

        section.setTitle(request.getTitle().trim());
        section.setOrderIndex(request.getOrderIndex() == null ? section.getOrderIndex() : request.getOrderIndex());
        return toResponse(sectionRepository.save(section));
    }

    @Transactional
    public void delete(Long courseId, Long sectionId, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Section section = getInCourse(courseId, sectionId);
        permissionService.requireCourseWriteAccess(actor, section.getCourse());
        sectionRepository.delete(section);
    }

    Section getEntity(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
    }

    private Section getInCourse(Long courseId, Long sectionId) {
        Section section = getEntity(sectionId);
        if (!section.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Section not found");
        }
        return section;
    }

    private SectionResponse toResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .courseId(section.getCourse().getId())
                .title(section.getTitle())
                .orderIndex(section.getOrderIndex())
                .createdAt(section.getCreatedAt())
                .build();
    }
}

