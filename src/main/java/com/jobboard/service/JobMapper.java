package com.jobboard.service;

import com.jobboard.dto.response.JobResponse;
import com.jobboard.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryCurrency(job.getSalaryCurrency())
                .status(job.getStatus())
                .deadline(job.getDeadline())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .employerId(job.getEmployer().getId())
                .employerName(job.getEmployer().getFullName())
                .companyName(job.getEmployer().getCompanyName())
                .build();
    }
}
