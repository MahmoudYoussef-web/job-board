package com.jobboard.controller;

import com.jobboard.dto.request.JobRequest;
import com.jobboard.dto.request.JobSearchRequest;
import com.jobboard.dto.response.JobResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.JobService;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // ----------------------------------------------------------------
    // Public endpoints (no auth required — permitted in SecurityConfig)
    // ----------------------------------------------------------------

    /**
     * Search open jobs with optional filters.
     * GET /api/jobs?title=java&location=cairo&salaryMin=1000&page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<JobResponse>> searchJobs(
            JobSearchRequest filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(jobService.searchJobs(filter, pageable));
    }

    /**
     * Get a single open job by ID.
     * GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    // ----------------------------------------------------------------
    // Employer endpoints
    // ----------------------------------------------------------------

    /**
     * Create a new job posting.
     * POST /api/jobs
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(principal.getId(), request));
    }

    /**
     * Update an existing job (employer must own it).
     * PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(jobService.updateJob(principal.getId(), id, request));
    }

    /**
     * Close a job (stops new applications).
     * PATCH /api/jobs/{id}/close
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(jobService.closeJob(principal.getId(), id));
    }

    /**
     * Delete a job posting.
     * DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        jobService.deleteJob(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Employer's own job listings.
     * GET /api/jobs/mine
     */
    @GetMapping("/mine")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Page<JobResponse>> getMyJobs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(jobService.getMyJobs(principal.getId(), pageable));
    }
}
