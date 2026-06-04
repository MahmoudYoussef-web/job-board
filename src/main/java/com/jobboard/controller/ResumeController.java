package com.jobboard.controller;

import com.jobboard.entity.ResumeProfile;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeParserService resumeParserService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyResume(@AuthenticationPrincipal UserDetailsImpl principal) {
        ResumeProfile profile = resumeParserService.getLatest(principal.getId());
        if (profile == null) {
            return ResponseEntity.ok(Map.of("message", "No resume found. Upload one first."));
        }
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/me/refresh")
    public ResponseEntity<?> refreshMyResume(@AuthenticationPrincipal UserDetailsImpl principal) {
        ResumeProfile existing = resumeParserService.getLatest(principal.getId());
        if (existing == null || existing.getFileUrl() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No resume file found. Upload a resume first."));
        }

        ResumeProfile refreshed = resumeParserService.parseAndSave(principal.getId(), existing.getFileUrl());
        return ResponseEntity.ok(Map.of(
                "message", "Resume re-parsed successfully",
                "profile", refreshed
        ));
    }
}
