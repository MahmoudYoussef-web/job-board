package com.jobboard.dto.request;

import com.jobboard.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationStatusRequest {

    @NotNull
    private ApplicationStatus status;

    @Size(max = 2000)
    private String employerNotes;
}
