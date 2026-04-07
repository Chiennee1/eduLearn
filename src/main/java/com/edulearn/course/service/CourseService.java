package com.edulearn.course.service;

import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.common.Constants;
import com.edulearn.common.util.SlugUtils;
import com.edulearn.course.dto.CourseCreateRequest;
import com.edulearn.course.dto.CourseResponse;
import com.edulearn.course.dto.CourseUpdateRequest;
import com.edulearn.course.entity.Category;
import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.repository.CategoryRepository;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.SectionRepository;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final CoursePermissionService permissionService;

    @Transactional(readOnly = true)
    @Cacheable(value = Constants.CACHE_PUBLISHED_COURSES, key = "'all'")
    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatusOrderByPublishedAtDesc(CourseStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getPublishedCourseById(Long courseId) {
        Course course = getEntity(courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course not found");
        }
        return toResponse(course);
    }

    @Transactional(readOnly = true)
    public CourseResponse getPublishedCourseBySlug(String slug) {
        Course course = courseRepository.findBySlugAndStatus(slug, CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        permissionService.requireInstructorOrAdmin(actor);

        List<Course> courses;
        if (permissionService.isAdmin(actor)) {
            courses = courseRepository.findAll();
        } else {
            courses = courseRepository.findByInstructorIdOrderByCreatedAtDesc(actor.getId());
        }
        return courses.stream().map(this::toResponse).toList();
    }

    @Transactional
    @CacheEvict(value = Constants.CACHE_PUBLISHED_COURSES, allEntries = true)
    public CourseResponse create(CourseCreateRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        permissionService.requireInstructorOrAdmin(actor);

        User instructor = resolveInstructor(request.getInstructorId(), actor);
        String title = request.getTitle().trim();
        String slug = resolveSlug(request.getSlug(), title, null);

        Course course = Course.builder()
                .instructor(instructor)
                .title(title)
                .slug(slug)
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice())
                .level(request.getLevel())
                .language(request.getLanguage().trim())
                .durationHours(request.getDurationHours())
                .status(CourseStatus.DRAFT)
                .categories(resolveCategories(request.getCategoryIds()))
                .build();
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    @CacheEvict(value = Constants.CACHE_PUBLISHED_COURSES, allEntries = true)
    public CourseResponse update(Long courseId, CourseUpdateRequest request, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Course course = getEntity(courseId);
        permissionService.requireCourseWriteAccess(actor, course);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            course.setTitle(request.getTitle().trim());
            if (request.getSlug() == null || request.getSlug().isBlank()) {
                course.setSlug(resolveSlug(null, course.getTitle(), courseId));
            }
        }
        if (request.getSlug() != null) {
            course.setSlug(resolveSlug(request.getSlug(), course.getTitle(), courseId));
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getLevel() != null) {
            course.setLevel(request.getLevel());
        }
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            course.setLanguage(request.getLanguage().trim());
        }
        if (request.getDurationHours() != null) {
            course.setDurationHours(request.getDurationHours());
        }
        if (request.getCategoryIds() != null) {
            course.setCategories(resolveCategories(request.getCategoryIds()));
        }

        return toResponse(courseRepository.save(course));
    }

    @Transactional
    @CacheEvict(value = Constants.CACHE_PUBLISHED_COURSES, allEntries = true)
    public CourseResponse publish(Long courseId, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Course course = getEntity(courseId);
        permissionService.requireCourseWriteAccess(actor, course);

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            return toResponse(course);
        }
        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new BusinessException("Archived course cannot be published", HttpStatus.CONFLICT);
        }

        long sectionCount = sectionRepository.countByCourseId(courseId);
        if (sectionCount == 0) {
            throw new BusinessException("Course must have at least one section before publishing", HttpStatus.BAD_REQUEST);
        }

        List<Long> invalidSectionIds = sectionRepository.findSectionIdsWithoutLessons(courseId);
        if (!invalidSectionIds.isEmpty()) {
            throw new BusinessException("Each section must have at least one lesson before publishing", HttpStatus.BAD_REQUEST);
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    @CacheEvict(value = Constants.CACHE_PUBLISHED_COURSES, allEntries = true)
    public CourseResponse archive(Long courseId, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Course course = getEntity(courseId);
        permissionService.requireCourseWriteAccess(actor, course);

        course.setStatus(CourseStatus.ARCHIVED);
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    @CacheEvict(value = Constants.CACHE_PUBLISHED_COURSES, allEntries = true)
    public void delete(Long courseId, String actorEmail) {
        User actor = permissionService.getActor(actorEmail);
        Course course = getEntity(courseId);
        permissionService.requireCourseWriteAccess(actor, course);
        courseRepository.delete(course);
    }

    Course getEntity(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private User resolveInstructor(Long requestedInstructorId, User actor) {
        if (!permissionService.isAdmin(actor)) {
            return actor;
        }
        if (requestedInstructorId == null) {
            return actor;
        }
        return userRepository.findById(requestedInstructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
    }

    private Set<Category> resolveCategories(Set<Integer> categoryIds) {
        Set<Integer> ids = categoryIds == null ? Set.of() : new HashSet<>(categoryIds);
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more categories not found");
        }
        return new HashSet<>(categories);
    }

    private String resolveSlug(String rawSlug, String title, Long courseId) {
        String base = (rawSlug == null || rawSlug.isBlank()) ? title : rawSlug;
        String slug = SlugUtils.toSlug(base);
        if (slug.isBlank()) {
            throw new BusinessException("Course slug is invalid", HttpStatus.BAD_REQUEST);
        }

        boolean exists = courseId == null
                ? courseRepository.existsBySlug(slug)
                : courseRepository.existsBySlugAndIdNot(slug, courseId);
        if (exists) {
            throw new BusinessException("Course slug already exists", HttpStatus.CONFLICT);
        }
        return slug;
    }

    private CourseResponse toResponse(Course course) {
        Set<Integer> categoryIds = new LinkedHashSet<>();
        if (course.getCategories() != null) {
            course.getCategories().stream()
                    .map(Category::getId)
                    .forEach(categoryIds::add);
        }

        return CourseResponse.builder()
                .id(course.getId())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .level(course.getLevel())
                .status(course.getStatus())
                .language(course.getLanguage())
                .durationHours(course.getDurationHours())
                .publishedAt(course.getPublishedAt())
                .categoryIds(categoryIds)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}

