package com.jobboard.controller;

import com.jobboard.dto.response.EmployerDashboardResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerDashboardResponse> getEmployerDashboard(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(
                dashboardService.getEmployerDashboard(principal.getId()));
    }
}
