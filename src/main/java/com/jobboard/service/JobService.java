package com.jobboard.service;

import com.jobboard.dto.request.JobRequest;
import com.jobboard.dto.request.JobSearchRequest;
import com.jobboard.dto.request.JobStatusRequest;
import com.jobboard.dto.response.JobResponse;
import com.jobboard.entity.Job;
import com.jobboard.entity.User;
import com.jobboard.enums.JobStatus;
import com.jobboard.exception.ForbiddenOperationException;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.repository.JobRepository;
import com.jobboard.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final UserService   userService;
    private final JobMapper     jobMapper;

    // ----------------------------------------------------------------
    // Public search (open to all)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(JobSearchRequest filter, Pageable pageable) {
        return jobRepository
                .findAll(JobSpecification.fromFilter(filter), pageable)
                .map(jobMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public JobResponse getById(Long jobId) {
        Job job = jobRepository.findOpenJobByIdWithEmployer(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
        return jobMapper.toResponse(job);
    }

    // ----------------------------------------------------------------
    // Employer CRUD
    // ----------------------------------------------------------------

    @Transactional
    public JobResponse createJob(Long employerId, JobRequest request) {
        User employer = userService.findById(employerId);

        Job job = Job.builder()
                .employer(employer)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .experienceLevel(request.getExperienceLevel())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .salaryCurrency(request.getSalaryCurrency() != null ? request.getSalaryCurrency() : "USD")
                .deadline(request.getDeadline())
                .status(JobStatus.OPEN)
                .build();

        Job saved = jobRepository.save(job);
        log.info("Job created [id={}] by employer [id={}]", saved.getId(), employerId);
        return jobMapper.toResponse(saved);
    }

    @Transactional
    public JobResponse updateJob(Long employerId, Long jobId, JobRequest request) {
        Job job = findJobOwnedBy(employerId, jobId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        if (request.getSalaryCurrency() != null) {
            job.setSalaryCurrency(request.getSalaryCurrency());
        }
        job.setDeadline(request.getDeadline());

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long employerId, Long jobId) {
        Job job = findJobOwnedBy(employerId, jobId);
        jobRepository.delete(job);
        log.info("Job deleted [id={}] by employer [id={}]", jobId, employerId);
    }

    /**
     * Convenience shorthand to close a job (OPEN → CLOSED).
     * Kept for backward-compatibility; updateStatus() is the general-purpose version.
     */
    @Transactional
    public JobResponse closeJob(Long employerId, Long jobId) {
        Job job = findJobOwnedBy(employerId, jobId);
        job.setStatus(JobStatus.CLOSED);
        log.info("Job closed [id={}] by employer [id={}]", jobId, employerId);
        return jobMapper.toResponse(jobRepository.save(job));
    }

    /**
     * Generic status transition for a job owned by the employer.
     * Supported transitions:
     *   DRAFT  → OPEN    (publish)
     *   OPEN   → CLOSED  (close)
     *   OPEN   → DRAFT   (un-publish)
     *   CLOSED → OPEN    (re-open)
     */
    @Transactional
    public JobResponse updateStatus(Long employerId, Long jobId, JobStatusRequest request) {
        Job job = findJobOwnedBy(employerId, jobId);
        JobStatus previous = job.getStatus();
        job.setStatus(request.getStatus());
        log.info("Job [id={}] status: {} → {} by employer [id={}]",
                 jobId, previous, request.getStatus(), employerId);
        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(Long employerId, Pageable pageable) {
        return jobRepository.findByEmployerId(employerId, pageable)
                .map(jobMapper::toResponse);
    }

    // ----------------------------------------------------------------
    // Package-accessible helper used by ApplicationService
    // ----------------------------------------------------------------

    Job findOpenJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .filter(j -> j.getStatus() == JobStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found or not open with id: " + jobId));
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private Job findJobOwnedBy(Long employerId, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new ForbiddenOperationException("You do not own this job posting");
        }
        return job;
    }
}
