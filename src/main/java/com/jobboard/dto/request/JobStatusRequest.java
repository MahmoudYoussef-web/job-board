package com.jobboard.dto.request;

import com.jobboard.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for an employer to change the status of their own job posting.
 * Supported transitions (employer-initiated):
 *   DRAFT → OPEN       (publish a draft)
 *   OPEN  → CLOSED     (stop accepting applications)
 *   OPEN  → DRAFT      (un-publish / put back to draft)
 *   CLOSED → OPEN      (re-open a closed posting)
 */
@Data
public class JobStatusRequest {

    @NotNull(message = "Status must not be null")
    private JobStatus status;
}
