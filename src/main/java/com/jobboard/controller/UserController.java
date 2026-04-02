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

   
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(userService.getProfile(principal.getId()));
    }

   
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }
}
