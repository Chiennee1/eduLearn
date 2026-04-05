package com.edulearn.course.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.course.dto.LearningDashboardResponse;
import com.edulearn.course.service.LearningDashboardFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.LEARNING_API_PREFIX)
public class LearningDashboardController {

    private final LearningDashboardFacade learningDashboardFacade;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<LearningDashboardResponse>> getMyDashboard(Authentication authentication) {
        LearningDashboardResponse response = learningDashboardFacade.getStudentDashboard(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Learning dashboard fetched", response));
    }
}


