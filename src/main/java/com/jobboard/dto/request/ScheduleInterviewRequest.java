package com.jobboard.dto.request;

import com.jobboard.enums.InterviewType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleInterviewRequest {

    @NotNull
    private Long applicationId;

    @NotNull
    @Future
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    private String meetingLink;

    private String meetingPlatform;

    private String location;

    private InterviewType type;
}
