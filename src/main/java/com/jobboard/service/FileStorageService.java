package com.jobboard.service;

import com.jobboard.exception.FileUploadException;
import com.jobboard.service.event.ResumeUploadedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PDF_EXTENSION    = ".pdf";
    private static final long   MAX_SIZE_BYTES   = 2 * 1024 * 1024L;

    @Value("${app.upload.dir:./uploads/resumes}")
    private String uploadDir;

    private Path uploadPath;

    private final ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            log.info("File upload directory ready: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    public String storeResume(MultipartFile file, Long userId) {
        validatePdf(file);

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "resume.pdf"
        );

        if (originalName.contains("..")) {
            throw new FileUploadException("Filename contains invalid path sequence: " + originalName);
        }

        String storedName = UUID.randomUUID() + PDF_EXTENSION;
        Path targetLocation = uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file {}: {}", storedName, e.getMessage());
            throw new FileUploadException("Could not store file. Please try again.");
        }

        String storedPath = uploadDir + "/" + storedName;

        eventPublisher.publishEvent(new ResumeUploadedEvent(userId, storedPath));

        return storedPath;
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path target = Paths.get(filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", filePath, e.getMessage());
        }
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File must not be empty");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new FileUploadException(
                    "File size " + (file.getSize() / 1024 / 1024) + "MB exceeds the 2MB limit"
            );
        }

        String contentType = file.getContentType();
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new FileUploadException(
                    "Only PDF files are accepted. Received: " + contentType
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !originalFilename.toLowerCase().endsWith(PDF_EXTENSION)) {
            throw new FileUploadException("File must have a .pdf extension");
        }
    }

}