package com.jobboard.service;

import com.jobboard.dto.response.ApplicationResponse;
import com.jobboard.entity.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    /** Full response including employer notes — for EMPLOYER views */
    public ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .jobLocation(app.getJob().getLocation())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getFullName())
                .candidateEmail(app.getCandidate().getEmail())
                .coverLetter(app.getCoverLetter())
                .resumeUrl(app.getResumeUrl())
                .status(app.getStatus())
                .employerNotes(app.getEmployerNotes())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    /** Candidate-safe response — strips employer notes */
    public ApplicationResponse toCandidateResponse(Application app) {
        ApplicationResponse response = toResponse(app);
        response.setEmployerNotes(null);
        return response;
    }
}
