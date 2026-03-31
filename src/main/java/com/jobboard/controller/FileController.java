package com.jobboard.controller;

import com.jobboard.dto.response.FileUploadResponse;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.FileStorageService;
import com.jobboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserService userService;

    @PostMapping("/resume")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<FileUploadResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        String storedPath = fileStorageService.storeResume(file, principal.getId());

        userService.updateResumeUrl(principal.getId(), storedPath);

        FileUploadResponse response = FileUploadResponse.builder()
                .fileName(file.getOriginalFilename())
                .filePath(storedPath)
                .sizeBytes(file.getSize())
                .message("Resume uploaded successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}