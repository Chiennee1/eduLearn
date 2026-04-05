package com.edulearn.admin.controller;

import com.edulearn.admin.dto.AdminDashboardStatsResponse;
import com.edulearn.admin.service.AdminDashboardService;
import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.ADMIN_API_PREFIX)
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getDashboardStats() {
        AdminDashboardStatsResponse response = adminDashboardService.getStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard stats fetched", response));
    }
}

