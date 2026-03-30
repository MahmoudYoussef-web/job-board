package com.jobboard.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumeUploadedEvent {
    private Long userId;
    private String filePath;
}