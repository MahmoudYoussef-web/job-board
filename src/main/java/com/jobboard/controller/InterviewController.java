package com.jobboard.controller;

import com.jobboard.dto.request.InterviewFeedbackRequest;
import com.jobboard.dto.request.ScheduleInterviewRequest;
import com.jobboard.dto.response.InterviewResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.InterviewService;
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
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<InterviewResponse> schedule(
            @Valid @RequestBody ScheduleInterviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.schedule(principal.getId(), request));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Page<InterviewResponse>> getMyInterviews(
            @PageableDefault(size = 10, sort = "scheduledAt", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(interviewService.getMyInterviews(principal.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Page<InterviewResponse>> getInterviewsForApplication(
            @RequestParam Long applicationId,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(
                interviewService.getInterviewsForApplication(principal.getId(), applicationId, pageable));
    }

    @PatchMapping("/{id}/feedback")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<InterviewResponse> giveFeedback(
            @PathVariable Long id,
            @Valid @RequestBody InterviewFeedbackRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(interviewService.giveFeedback(principal.getId(), id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<InterviewResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(interviewService.cancel(principal.getId(), id));
    }
}
