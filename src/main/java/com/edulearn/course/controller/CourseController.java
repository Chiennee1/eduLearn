package com.edulearn.course.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.course.dto.CourseCreateRequest;
import com.edulearn.course.dto.CourseResponse;
import com.edulearn.course.dto.CourseUpdateRequest;
import com.edulearn.course.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.COURSE_API_PREFIX)
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getPublishedCourses() {
        return ResponseEntity.ok(ApiResponse.success("Published courses fetched", courseService.getPublishedCourses()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("My courses fetched", courseService.getMyCourses(authentication.getName())));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getPublishedCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Course fetched", courseService.getPublishedCourseById(courseId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Course created", courseService.create(request, authentication.getName())));
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Course updated", courseService.update(courseId, request, authentication.getName())));
    }

    @PostMapping("/{courseId}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> publish(@PathVariable Long courseId, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Course published", courseService.publish(courseId, authentication.getName())));
    }

    @PostMapping("/{courseId}/archive")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> archive(@PathVariable Long courseId, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Course archived", courseService.archive(courseId, authentication.getName())));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long courseId, Authentication authentication) {
        courseService.delete(courseId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Course deleted", null));
    }
}

