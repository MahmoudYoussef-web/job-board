package com.jobboard.dto.response;

import com.jobboard.enums.ExperienceLevel;
import com.jobboard.enums.JobStatus;
import com.jobboard.enums.JobType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private JobStatus status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Employer summary — avoids exposing full User entity */
    private Long employerId;
    private String employerName;
    private String companyName;
}
