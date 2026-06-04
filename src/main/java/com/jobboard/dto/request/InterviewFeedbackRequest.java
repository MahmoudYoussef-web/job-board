package com.jobboard.dto.request;

import com.jobboard.enums.InterviewResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class InterviewFeedbackRequest {

    private String feedback;

    @Min(1)
    @Max(5)
    private Integer rating;

    private InterviewResult result;
}
