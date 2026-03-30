package com.jobboard.service;

import com.jobboard.entity.CvAnalysis;
import com.jobboard.entity.Job;
import com.jobboard.enums.MatchLevel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchingService {

    private static final List<String> KEYWORDS = List.of(
            "java", "spring", "sql", "rest", "docker", "kubernetes"
    );

    public MatchResult calculateMatch(Job job, CvAnalysis cv) {

        Set<String> jobSkills = extractJobSkills(job.getRequirements());
        Set<String> userSkills = extractUserSkills(cv.getExtractedSkills());

        Set<String> matchedSkills = new HashSet<>(jobSkills);
        matchedSkills.retainAll(userSkills);

        int score = jobSkills.isEmpty() ? 0 :
                (matchedSkills.size() * 100) / jobSkills.size();

        MatchLevel level = score >= 80 ? MatchLevel.HIGH :
                score >= 50 ? MatchLevel.MEDIUM : MatchLevel.LOW;

        log.info("Matching result: jobSkills={}, userSkills={}, matched={}, score={}",
                jobSkills, userSkills, matchedSkills, score);

        return MatchResult.builder()
                .score(score)
                .matchLevel(level)
                .matchedSkills(matchedSkills)
                .build();
    }

    private Set<String> extractJobSkills(String requirements) {
        if (requirements == null) return Collections.emptySet();

        String normalized = requirements.toLowerCase();

        return KEYWORDS.stream()
                .filter(normalized::contains)
                .collect(Collectors.toSet());
    }

    private Set<String> extractUserSkills(String skills) {
        if (skills == null || skills.isBlank()) return Collections.emptySet();

        return Arrays.stream(skills.toLowerCase().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Data
    @Builder
    public static class MatchResult {
        private int score;
        private MatchLevel matchLevel;
        private Set<String> matchedSkills;
    }

}