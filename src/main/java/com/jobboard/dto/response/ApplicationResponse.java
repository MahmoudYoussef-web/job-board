package com.jobboard.dto.response;

import com.jobboard.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {

    private Long id;

    // Job summary
    private Long jobId;
    private String jobTitle;
    private String jobLocation;

    // Candidate summary
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private String coverLetter;
    private String resumeUrl;
    private ApplicationStatus status;

    /**
     * Employer-only field.
     * Set to null before returning to candidates (see ApplicationMapper).
     */
    private String employerNotes;

    private Integer applicationScore;
    private String matchLevel;
    private String missingSkills;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
