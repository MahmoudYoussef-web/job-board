package com.jobboard.service.event;

import com.jobboard.service.CvAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvAnalysisListener {

    private final CvAnalysisService cvAnalysisService;

    @Async
    @EventListener
    public void handle(ResumeUploadedEvent event) {
        cvAnalysisService.analyzeAndSave(event.getUserId(), event.getFilePath());
        log.info("Async CV analysis done for userId={}", event.getUserId());
    }

}