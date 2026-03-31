package com.jobboard.controller;

import com.jobboard.dto.request.ApplicationRequest;
import com.jobboard.dto.request.ApplicationStatusRequest;
import com.jobboard.dto.response.ApplicationResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.ApplicationService;
import com.jobboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

        private final ApplicationService applicationService;
        private final UserService userService;

        @PostMapping("/api/jobs/{jobId}/apply")
        @PreAuthorize("hasRole('CANDIDATE')")
        public ResponseEntity<ApplicationResponse> apply(
                        @PathVariable Long jobId,
                        @Valid @RequestBody ApplicationRequest request,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                String resumeUrl = userService.findById(principal.getId()).getResumeUrl();

                ApplicationResponse response = applicationService.apply(
                                principal.getId(), jobId, request, resumeUrl);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/api/applications/me")
        @PreAuthorize("hasRole('CANDIDATE')")
        public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
                        @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                return ResponseEntity.ok(
                                applicationService.getMyApplications(principal.getId(), pageable));
        }

        @GetMapping("/api/applications/me/{applicationId}")
        @PreAuthorize("hasRole('CANDIDATE')")
        public ResponseEntity<ApplicationResponse> getMyApplication(
                        @PathVariable Long applicationId,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                return ResponseEntity.ok(
                                applicationService.getMyApplication(principal.getId(), applicationId));
        }

        @GetMapping("/api/jobs/{jobId}/applications")
        @PreAuthorize("hasRole('EMPLOYER')")
        public ResponseEntity<Page<ApplicationResponse>> getApplicationsForJob(
                        @PathVariable Long jobId,
                        @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                return ResponseEntity.ok(
                                applicationService.getApplicationsForJob(principal.getId(), jobId, pageable));
        }

        @GetMapping("/api/applications")
        @PreAuthorize("hasRole('EMPLOYER')")
        public ResponseEntity<Page<ApplicationResponse>> getAllApplicationsForEmployer(
                        @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                return ResponseEntity.ok(
                                applicationService.getAllApplicationsForEmployer(principal.getId(), pageable));
        }

        @PatchMapping("/api/applications/{applicationId}/status")
        @PreAuthorize("hasRole('EMPLOYER')")
        public ResponseEntity<ApplicationResponse> updateStatus(
                        @PathVariable Long applicationId,
                        @Valid @RequestBody ApplicationStatusRequest request,
                        @AuthenticationPrincipal UserDetailsImpl principal) {

                return ResponseEntity.ok(
                                applicationService.updateStatus(principal.getId(), applicationId, request));
        }

}