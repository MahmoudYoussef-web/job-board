package com.jobboard.service;

import com.jobboard.dto.response.EmployerDashboardResponse;
import com.jobboard.dto.response.EmployerDashboardResponse.TopCandidate;
import com.jobboard.entity.Application;
import com.jobboard.enums.ApplicationStatus;
import com.jobboard.enums.InterviewStatus;
import com.jobboard.enums.JobStatus;
import com.jobboard.repository.ApplicationRepository;
import com.jobboard.repository.InterviewRepository;
import com.jobboard.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    @Transactional(readOnly = true)
    public EmployerDashboardResponse getEmployerDashboard(Long employerId) {
        long openJobs = jobRepository.countByEmployerIdAndStatus(employerId, JobStatus.OPEN);

        long totalApplications = applicationRepository.countByEmployerId(employerId);

        long scheduledInterviews = interviewRepository.countScheduledByEmployerId(employerId,
                InterviewStatus.SCHEDULED);

        long hiredCount = applicationRepository.countByEmployerIdAndStatus(employerId,
                ApplicationStatus.HIRED);

        List<Application> topApps = applicationRepository
                .findTopByEmployerIdOrderByScoreDesc(employerId, PageRequest.of(0, 5));

        List<TopCandidate> topCandidates = topApps.stream()
                .filter(a -> a.getApplicationScore() != null)
                .map(a -> TopCandidate.builder()
                        .applicationId(a.getId())
                        .candidateId(a.getCandidate().getId())
                        .candidateName(a.getCandidate().getFullName())
                        .jobId(a.getJob().getId())
                        .jobTitle(a.getJob().getTitle())
                        .score(a.getApplicationScore())
                        .matchLevel(a.getMatchLevel() != null ? a.getMatchLevel().name() : null)
                        .build())
                .collect(Collectors.toList());

        return EmployerDashboardResponse.builder()
                .openJobs(openJobs)
                .totalApplications(totalApplications)
                .scheduledInterviews(scheduledInterviews)
                .hiredCount(hiredCount)
                .topCandidates(topCandidates)
                .build();
    }
}
