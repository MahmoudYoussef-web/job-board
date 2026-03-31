package com.jobboard.controller;

import com.jobboard.dto.request.UpdateProfileRequest;
import com.jobboard.dto.response.UserResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns the authenticated user's own profile.
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(userService.getProfile(principal.getId()));
    }

    /**
     * Updates mutable fields on the authenticated user's own profile.
     * Email, password, and role changes require dedicated flows.
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }
}
