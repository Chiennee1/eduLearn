package com.edulearn.course.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.course.dto.SectionRequest;
import com.edulearn.course.dto.SectionResponse;
import com.edulearn.course.service.SectionService;
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
@RequestMapping(Constants.COURSE_API_PREFIX + "/{courseId}/sections")
public class SectionController {

    private final SectionService sectionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getPublicByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Sections fetched", sectionService.getPublicByCourseId(courseId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> create(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Section created", sectionService.create(courseId, request, authentication.getName())));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> update(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Section updated", sectionService.update(courseId, sectionId, request, authentication.getName())));
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            Authentication authentication
    ) {
        sectionService.delete(courseId, sectionId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Section deleted", null));
    }
}

