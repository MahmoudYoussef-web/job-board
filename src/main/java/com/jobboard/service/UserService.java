package com.jobboard.service;

import com.jobboard.dto.request.UpdateProfileRequest;
import com.jobboard.dto.response.UserResponse;
import com.jobboard.entity.User;
import com.jobboard.enums.Role;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = findById(userId);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateResumeUrl(Long userId, String resumeUrl) {
        User user = findById(userId);
        user.setResumeUrl(resumeUrl);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findById(userId);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getCompanyName() != null && user.getRole() == Role.EMPLOYER) {
            user.setCompanyName(request.getCompanyName());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

}