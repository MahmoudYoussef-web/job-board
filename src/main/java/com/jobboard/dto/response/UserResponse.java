package com.jobboard.dto.response;

import com.jobboard.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String phone;
    private String companyName;   // non-null for EMPLOYER
    private String resumeUrl;     // non-null for CANDIDATE
    private String profilePictureUrl;
    private boolean active;
    private LocalDateTime createdAt;
}
