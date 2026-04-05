package com.edulearn.review.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.common.PageResponse;
import com.edulearn.exception.BusinessException;
import com.edulearn.review.dto.ReviewRequest;
import com.edulearn.review.dto.ReviewResponse;
import com.edulearn.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_PREFIX)
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getByCourse(
            @PathVariable Long courseId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        validateReviewSort(sortBy, sortDir);
        String actorEmail = authentication != null ? authentication.getName() : null;
        PageResponse<ReviewResponse> response = reviewService.getByCourse(
                courseId,
                actorEmail,
                page,
                size,
                sortBy,
                sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", response));
    }

    @PostMapping("/courses/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createOrUpdate(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        ReviewResponse response = reviewService.createOrUpdate(courseId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review saved", response));
    }

    @DeleteMapping("/courses/{courseId}/reviews/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> deleteMyReview(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        reviewService.deleteMyReview(courseId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }

    @PostMapping("/reviews/{reviewId}/like")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> like(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        ReviewResponse response = reviewService.like(reviewId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review liked", response));
    }

    @DeleteMapping("/reviews/{reviewId}/like")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> unlike(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        ReviewResponse response = reviewService.unlike(reviewId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review unliked", response));
    }

    private void validateReviewSort(String sortBy, String sortDir) {
        Set<String> allowedSortBy = Set.of("createdAt", "updatedAt", "rating");
        if (!allowedSortBy.contains(sortBy)) {
            throw new BusinessException("Invalid sortBy. Allowed values: createdAt, updatedAt, rating", HttpStatus.BAD_REQUEST);
        }
        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            throw new BusinessException("Invalid sortDir. Allowed values: asc, desc", HttpStatus.BAD_REQUEST);
        }
    }
}


