package com.jobboard.dto.response;

import com.jobboard.enums.InterviewResult;
import com.jobboard.enums.InterviewStatus;
import com.jobboard.enums.InterviewType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InterviewResponse {

    private Long id;

    private Long applicationId;
    private String jobTitle;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private Long interviewerId;
    private String interviewerName;

    private LocalDateTime scheduledAt;
    private Integer durationMinutes;

    private String meetingLink;
    private String meetingPlatform;
    private String location;

    private InterviewStatus status;
    private InterviewType type;

    private String feedback;
    private Integer rating;
    private InterviewResult result;

    private LocalDateTime createdAt;
}
