package com.jobboard.service.event;

import com.jobboard.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvAnalysisListener {

    private final ResumeParserService resumeParserService;

    @Async
    @EventListener
    public void handle(ResumeUploadedEvent event) {
        resumeParserService.parseAndSave(event.getUserId(), event.getFilePath());
        log.info("Resume parsed for userId={}", event.getUserId());
    }

}