package com.jobboard.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for updating the authenticated user's own profile.
 * Only mutable, non-sensitive fields are exposed here.
 * Email, password, and role changes require dedicated endpoints.
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    /** Employers only — ignored silently for CANDIDATE accounts */
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;
}
