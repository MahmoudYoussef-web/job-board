package com.jobboard.dto.request;

import com.jobboard.enums.ExperienceLevel;
import com.jobboard.enums.JobType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchRequest {

    /** Matches against title (case-insensitive LIKE) */
    private String title;

    /** Matches against location (case-insensitive LIKE) */
    private String location;

    private JobType jobType;

    private ExperienceLevel experienceLevel;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;
}
