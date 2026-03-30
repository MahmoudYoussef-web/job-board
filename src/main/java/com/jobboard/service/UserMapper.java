package com.jobboard.service;

import com.jobboard.dto.response.UserResponse;
import com.jobboard.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .companyName(user.getCompanyName())
                .resumeUrl(user.getResumeUrl())
                .profilePictureUrl(user.getProfilePictureUrl())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
