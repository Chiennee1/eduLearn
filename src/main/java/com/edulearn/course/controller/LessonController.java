package com.edulearn.course.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.course.dto.LessonRequest;
import com.edulearn.course.dto.LessonResponse;
import com.edulearn.course.service.LessonService;
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
@RequestMapping(Constants.API_V1_PREFIX)
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getPublicBySectionId(@PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.success("Lessons fetched", lessonService.getPublicBySectionId(sectionId)));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getPublicById(@PathVariable Long lessonId) {
        return ResponseEntity.ok(ApiResponse.success("Lesson fetched", lessonService.getPublicById(lessonId)));
    }

    @PostMapping("/sections/{sectionId}/lessons")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> create(
            @PathVariable Long sectionId,
            @Valid @RequestBody LessonRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Lesson created", lessonService.create(sectionId, request, authentication.getName())));
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> update(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Lesson updated", lessonService.update(lessonId, request, authentication.getName())));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long lessonId, Authentication authentication) {
        lessonService.delete(lessonId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted", null));
    }
}
