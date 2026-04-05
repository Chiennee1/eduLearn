package com.edulearn.review.service;

import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.course.entity.Course;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import com.edulearn.review.dto.ReviewRequest;
import com.edulearn.review.dto.ReviewResponse;
import com.edulearn.review.entity.Review;
import com.edulearn.review.entity.ReviewLike;
import com.edulearn.review.entity.ReviewLikeId;
import com.edulearn.review.repository.ReviewLikeRepository;
import com.edulearn.review.repository.ReviewRepository;
import com.edulearn.common.PageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseStatsService courseStatsService;

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByCourse(
            Long courseId,
            String actorEmail,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Long actorId = resolveActorId(actorEmail);
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Page<Review> sourcePage = reviewRepository.findByCourseId(courseId, pageable);
        List<ReviewResponse> content = sourcePage.getContent().stream()
                .map(review -> toResponse(review, actorId))
                .toList();
        Page<ReviewResponse> mappedPage = new org.springframework.data.domain.PageImpl<>(
                content,
                pageable,
                sourcePage.getTotalElements()
        );
        return PageResponse.from(mappedPage);
    }

    @Transactional
    public ReviewResponse createOrUpdate(Long courseId, ReviewRequest request, String actorEmail) {
        User actor = getStudentActor(actorEmail);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!enrollmentRepository.existsByUserIdAndCourseId(actor.getId(), courseId)) {
            throw new BusinessException("You must enroll before reviewing", HttpStatus.FORBIDDEN);
        }

        Review review = reviewRepository.findByUserIdAndCourseId(actor.getId(), courseId)
                .orElse(Review.builder().user(actor).course(course).build());

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review = reviewRepository.save(review);

        courseStatsService.recomputeCourseStats(courseId);
        return toResponse(review, actor.getId());
    }

    @Transactional
    public void deleteMyReview(Long courseId, String actorEmail) {
        User actor = getStudentActor(actorEmail);
        Review review = reviewRepository.findByUserIdAndCourseId(actor.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
        courseStatsService.recomputeCourseStats(courseId);
    }

    @Transactional
    public ReviewResponse like(Long reviewId, String actorEmail) {
        User actor = getStudentActor(actorEmail);
        Review review = getReview(reviewId);

        ReviewLikeId id = new ReviewLikeId(actor.getId(), reviewId);
        if (!reviewLikeRepository.existsById(id)) {
            reviewLikeRepository.save(ReviewLike.builder()
                    .id(id)
                    .user(actor)
                    .review(review)
                    .build());
        }
        return toResponse(review, actor.getId());
    }

    @Transactional
    public ReviewResponse unlike(Long reviewId, String actorEmail) {
        User actor = getStudentActor(actorEmail);
        Review review = getReview(reviewId);

        ReviewLikeId id = new ReviewLikeId(actor.getId(), reviewId);
        if (reviewLikeRepository.existsById(id)) {
            reviewLikeRepository.deleteById(id);
        }
        return toResponse(review, actor.getId());
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    private User getStudentActor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isStudent = user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.STUDENT);
        if (!isStudent) {
            throw new BusinessException("Only STUDENT can perform this action", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private Long resolveActorId(String actorEmail) {
        if (actorEmail == null || actorEmail.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(actorEmail)
                .map(User::getId)
                .orElse(null);
    }

    private ReviewResponse toResponse(Review review, Long actorId) {
        long likeCount = reviewLikeRepository.countByReview_Id(review.getId());
        boolean likedByMe = actorId != null && reviewLikeRepository.existsByReview_IdAndUser_Id(review.getId(), actorId);

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .fullName(review.getUser().getFullName())
                .rating(review.getRating())
                .content(review.getContent())
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String normalizedSortBy = switch (sortBy == null ? "createdAt" : sortBy) {
            case "rating" -> "rating";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, normalizedSortBy);
    }
}

