package com.jobboard.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationRequest {

    @Size(max = 5000)
    private String coverLetter;
}
