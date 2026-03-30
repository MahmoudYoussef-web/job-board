package com.jobboard.service;

import com.jobboard.dto.request.ApplicationRequest;
import com.jobboard.dto.request.ApplicationStatusRequest;
import com.jobboard.dto.response.ApplicationResponse;
import com.jobboard.entity.Application;
import com.jobboard.entity.CvAnalysis;
import com.jobboard.entity.Job;
import com.jobboard.entity.User;
import com.jobboard.enums.ApplicationStatus;
import com.jobboard.enums.JobStatus;
import com.jobboard.enums.Role;
import com.jobboard.exception.BusinessException;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.exception.UnauthorizedException;
import com.jobboard.repository.ApplicationRepository;
import com.jobboard.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserService userService;
    private final ApplicationMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CvAnalysisService cvAnalysisService;
    private final MatchingService matchingService;

    @Transactional
    public ApplicationResponse apply(Long candidateId,
                                     Long jobId,
                                     ApplicationRequest request,
                                     String resumeUrl) {

        User user = userService.findById(candidateId);

        if (user.getRole() != Role.CANDIDATE) {
            log.warn("Unauthorized apply attempt: userId={}", candidateId);
            throw new UnauthorizedException("Only candidates can apply to jobs");
        }

        if (resumeUrl == null || resumeUrl.isBlank()) {
            log.warn("Application rejected: no resume, candidateId={}", candidateId);
            throw new BusinessException("You must upload a resume before applying");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
            log.warn("Duplicate application attempt: jobId={}, candidateId={}", jobId, candidateId);
            throw new BusinessException("You already applied to this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (job.getStatus() != JobStatus.OPEN) {
            log.warn("Application rejected: job not open, jobId={}", jobId);
            throw new BusinessException("Cannot apply to closed job");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            log.warn("Application rejected: deadline passed, jobId={}", jobId);
            throw new BusinessException("Application deadline has passed");
        }

        CvAnalysis cv = cvAnalysisService.getLatest(candidateId);
        if (cv == null || cv.getScore() < 40) {
            log.warn("Application rejected: weak CV, candidateId={}", candidateId);
            throw new BusinessException("CV score too low");
        }

        MatchingService.MatchResult result = matchingService.calculateMatch(job, cv);

        Application app = Application.builder()
                .job(job)
                .candidate(user)
                .coverLetter(request.getCoverLetter())
                .resumeUrl(resumeUrl)
                .applicationScore(result.getScore())
                .matchLevel(result.getMatchLevel())
                .status(ApplicationStatus.PENDING)
                .build();

        Application saved = applicationRepository.save(app);

        log.info("Application created: jobId={}, candidateId={}, score={}, level={}",
                jobId, candidateId, result.getScore(), result.getMatchLevel());

        return mapper.toCandidateResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getMyApplications(Long candidateId, Pageable pageable) {
        return applicationRepository.findByCandidateId(candidateId, pageable)
                .map(mapper::toCandidateResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getMyApplication(Long candidateId, Long applicationId) {
        return applicationRepository.findByIdAndCandidateId(applicationId, candidateId)
                .map(mapper::toCandidateResponse)
                .orElseThrow(() -> new UnauthorizedException("Access denied"));
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForJob(Long employerId,
                                                           Long jobId,
                                                           Pageable pageable) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            log.warn("Unauthorized access to job applications: employerId={}, jobId={}", employerId, jobId);
            throw new UnauthorizedException("Not your job");
        }

        return applicationRepository.findByJobId(jobId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getAllApplicationsForEmployer(Long employerId,
                                                                   Pageable pageable) {

        return applicationRepository.findByEmployerId(employerId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long employerId,
                                            Long appId,
                                            ApplicationStatusRequest request) {

        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", appId));

        if (!app.getJob().getEmployer().getId().equals(employerId)) {
            log.warn("Unauthorized status update: employerId={}, appId={}", employerId, appId);
            throw new UnauthorizedException("You cannot update applications for this job");
        }

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException("Application already processed");
        }

        app.setStatus(request.getStatus());
        app.setEmployerNotes(request.getEmployerNotes());

        return mapper.toResponse(applicationRepository.save(app));
    }

}