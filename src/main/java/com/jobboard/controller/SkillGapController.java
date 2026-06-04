package com.jobboard.controller;

import com.jobboard.dto.response.SkillGapResponse;
import com.jobboard.entity.Job;
import com.jobboard.entity.ResumeProfile;
import com.jobboard.exception.BusinessException;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.repository.JobRepository;
import com.jobboard.repository.ResumeProfileRepository;
import com.jobboard.security.UserDetailsImpl;
import com.jobboard.service.SkillGapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/skill-gap")
@RequiredArgsConstructor
public class SkillGapController {

    private final SkillGapAnalysisService skillGapAnalysisService;
    private final ResumeProfileRepository resumeProfileRepository;
    private final JobRepository jobRepository;

    @GetMapping
    public ResponseEntity<?> analyzeSkillGap(
            @RequestParam Long jobId,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        ResumeProfile resume = resumeProfileRepository
                .findTopByCandidateIdAndIsPrimaryTrue(principal.getId())
                .orElse(null);

        if (resume == null) {
            return ResponseEntity.ok(SkillGapResponse.builder()
                    .matchScore(0)
                    .matchLevel("NONE")
                    .matchedSkills(Set.of())
                    .missingSkills(Set.of())
                    .candidateExtraSkills(Set.of())
                    .recommendations(Map.of())
                    .learningPath(List.of())
                    .build());
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        Set<String> jobSkills = skillGapAnalysisService.extractSkillsFromJob(job.getRequirements());
        Set<String> candidateSkills = skillGapAnalysisService.extractSkillsFromResume(resume.getSkills());

        SkillGapAnalysisService.AnalysisResult result =
                skillGapAnalysisService.analyze(jobSkills, candidateSkills);

        List<String> learningPath = new ArrayList<>();
        for (Map.Entry<String, String> rec : result.getRecommendations().entrySet()) {
            learningPath.add(rec.getKey() + " - " + rec.getValue());
        }

        return ResponseEntity.ok(SkillGapResponse.builder()
                .matchScore(result.getScore())
                .matchLevel(result.getMatchLevel().name())
                .matchedSkills(result.getMatchedSkills())
                .missingSkills(result.getMissingSkills())
                .candidateExtraSkills(result.getCandidateExtraSkills())
                .recommendations(result.getRecommendations())
                .learningPath(learningPath)
                .build());
    }
}
