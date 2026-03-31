package com.jobboard.dto.request;

import com.jobboard.enums.ExperienceLevel;
import com.jobboard.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    private String requirements;

    @NotBlank
    @Size(max = 200)
    private String location;

    @NotNull
    private JobType jobType;

    @NotNull
    private ExperienceLevel experienceLevel;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal salaryMax;

    @Size(max = 10)
    private String salaryCurrency = "USD";

    @Future
    private LocalDate deadline;

}