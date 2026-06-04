package com.jobboard.service;

import com.jobboard.dto.request.InterviewFeedbackRequest;
import com.jobboard.dto.request.ScheduleInterviewRequest;
import com.jobboard.dto.response.InterviewResponse;
import com.jobboard.entity.Application;
import com.jobboard.entity.Interview;
import com.jobboard.entity.User;
import com.jobboard.enums.ApplicationStatus;
import com.jobboard.enums.InterviewStatus;
import com.jobboard.exception.BusinessException;
import com.jobboard.exception.ForbiddenOperationException;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.repository.ApplicationRepository;
import com.jobboard.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    @Transactional
    public InterviewResponse schedule(Long employerId, ScheduleInterviewRequest request) {
        Application app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", request.getApplicationId()));

        if (!app.getJob().getEmployer().getId().equals(employerId)) {
            throw new ForbiddenOperationException("Not your application");
        }

        if (app.getStatus() == ApplicationStatus.PENDING) {
            throw new BusinessException("Application must pass screening first");
        }

        if (app.getStatus() == ApplicationStatus.REJECTED) {
            throw new BusinessException("Cannot interview a rejected candidate");
        }

        User interviewer = userService.findById(employerId);

        Interview interview = Interview.builder()
                .application(app)
                .candidate(app.getCandidate())
                .interviewer(interviewer)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .meetingLink(request.getMeetingLink())
                .meetingPlatform(request.getMeetingPlatform())
                .location(request.getLocation())
                .type(request.getType())
                .status(InterviewStatus.SCHEDULED)
                .build();

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponse giveFeedback(Long employerId, Long interviewId,
                                           InterviewFeedbackRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));

        if (!interview.getInterviewer().getId().equals(employerId)) {
            throw new ForbiddenOperationException("Not your interview");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new BusinessException("Can only give feedback for scheduled interviews");
        }

        interview.setFeedback(request.getFeedback());
        interview.setRating(request.getRating());
        interview.setResult(request.getResult());
        interview.setStatus(InterviewStatus.COMPLETED);

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponse cancel(Long employerId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));

        if (!interview.getInterviewer().getId().equals(employerId)) {
            throw new ForbiddenOperationException("Not your interview");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new BusinessException("Can only cancel scheduled interviews");
        }

        interview.setStatus(InterviewStatus.CANCELLED);

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponse> getMyInterviews(Long candidateId, Pageable pageable) {
        return interviewRepository.findByCandidateId(candidateId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponse> getInterviewsForApplication(Long employerId,
                                                                 Long applicationId,
                                                                 Pageable pageable) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (!app.getJob().getEmployer().getId().equals(employerId)) {
            throw new ForbiddenOperationException("Not your application");
        }

        return interviewRepository.findByApplicationId(applicationId, pageable)
                .map(this::toResponse);
    }

    private InterviewResponse toResponse(Interview interview) {
        return InterviewResponse.builder()
                .id(interview.getId())
                .applicationId(interview.getApplication().getId())
                .jobTitle(interview.getApplication().getJob().getTitle())
                .candidateId(interview.getCandidate().getId())
                .candidateName(interview.getCandidate().getFullName())
                .candidateEmail(interview.getCandidate().getEmail())
                .interviewerId(interview.getInterviewer().getId())
                .interviewerName(interview.getInterviewer().getFullName())
                .scheduledAt(interview.getScheduledAt())
                .durationMinutes(interview.getDurationMinutes())
                .meetingLink(interview.getMeetingLink())
                .meetingPlatform(interview.getMeetingPlatform())
                .location(interview.getLocation())
                .status(interview.getStatus())
                .type(interview.getType())
                .feedback(interview.getFeedback())
                .rating(interview.getRating())
                .result(interview.getResult())
                .createdAt(interview.getCreatedAt())
                .build();
    }
}
