package com.edulearn.quiz.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.common.PageResponse;
import com.edulearn.exception.BusinessException;
import com.edulearn.quiz.dto.QuizAttemptResponse;
import com.edulearn.quiz.dto.QuizCreateRequest;
import com.edulearn.quiz.dto.QuizOptionUpdateRequest;
import com.edulearn.quiz.dto.QuizQuestionUpdateRequest;
import com.edulearn.quiz.dto.QuizResponse;
import com.edulearn.quiz.dto.QuizSubmitRequest;
import com.edulearn.quiz.dto.QuizUpdateRequest;
import com.edulearn.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_PREFIX)
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/lessons/{lessonId}/quizzes")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> create(
            @PathVariable Long lessonId,
            @Valid @RequestBody QuizCreateRequest request,
            Authentication authentication
    ) {
        QuizResponse response = quizService.create(lessonId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quiz created", response));
    }

    @PutMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> update(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizUpdateRequest request,
            Authentication authentication
    ) {
        QuizResponse response = quizService.update(quizId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quiz updated", response));
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long quizId,
            Authentication authentication
    ) {
        quizService.delete(quizId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quiz deleted", null));
    }

    @PutMapping("/quizzes/{quizId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuizQuestionUpdateRequest request,
            Authentication authentication
    ) {
        QuizResponse response = quizService.updateQuestion(quizId, questionId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Question updated", response));
    }

    @PutMapping("/quizzes/{quizId}/questions/{questionId}/options/{optionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponse>> updateOption(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @Valid @RequestBody QuizOptionUpdateRequest request,
            Authentication authentication
    ) {
        QuizResponse response = quizService.updateOption(quizId, questionId, optionId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Option updated", response));
    }

    @DeleteMapping("/quizzes/{quizId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            Authentication authentication
    ) {
        quizService.deleteQuestion(quizId, questionId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Question deleted", null));
    }

    @DeleteMapping("/quizzes/{quizId}/questions/{questionId}/options/{optionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            Authentication authentication
    ) {
        quizService.deleteOption(quizId, questionId, optionId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Option deleted", null));
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> getById(@PathVariable Long quizId) {
        return ResponseEntity.ok(ApiResponse.success("Quiz fetched", quizService.getById(quizId)));
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> submit(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizSubmitRequest request,
            Authentication authentication
    ) {
        QuizAttemptResponse response = quizService.submit(quizId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quiz submitted", response));
    }

    @GetMapping("/quizzes/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<QuizAttemptResponse>>> getMyHistory(
            Authentication authentication,
            @RequestParam(required = false) Long quizId,
            @RequestParam(required = false) Boolean passed,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        validateHistorySort(sortBy, sortDir);
        PageResponse<QuizAttemptResponse> response = quizService.getMyHistory(
                authentication.getName(),
                quizId,
                passed,
                startedFrom,
                startedTo,
                page,
                size,
                sortBy,
                sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Quiz history fetched", response));
    }

    private void validateHistorySort(String sortBy, String sortDir) {
        Set<String> allowedSortBy = Set.of("startedAt", "submittedAt", "score");
        if (!allowedSortBy.contains(sortBy)) {
            throw new BusinessException("Invalid sortBy. Allowed values: startedAt, submittedAt, score", HttpStatus.BAD_REQUEST);
        }
        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            throw new BusinessException("Invalid sortDir. Allowed values: asc, desc", HttpStatus.BAD_REQUEST);
        }
    }
}


