package com.jobboard.service;

import com.jobboard.enums.MatchLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillGapAnalysisService {

    private final SkillDictionaryService skillDictionaryService;

    public AnalysisResult analyze(Set<String> jobSkills, Set<String> candidateSkills) {
        Set<String> expandedJob = expandSkills(jobSkills);
        Set<String> expandedCandidate = expandSkills(candidateSkills);

        Set<String> matched = new HashSet<>(expandedJob);
        matched.retainAll(expandedCandidate);

        Set<String> matchedDisplay = matched.stream()
                .map(skillDictionaryService::getDisplayName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> missing = new HashSet<>(expandedJob);
        missing.removeAll(expandedCandidate);

        Set<String> missingDisplay = missing.stream()
                .map(skillDictionaryService::getDisplayName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<MissingSkillInfo> missingWithImportance = missing.stream()
                .map(key -> MissingSkillInfo.builder()
                        .skill(skillDictionaryService.getDisplayName(key))
                        .importance(skillDictionaryService.getImportance(key))
                        .build())
                .collect(Collectors.toList());

        Set<String> extra = new HashSet<>(expandedCandidate);
        extra.removeAll(expandedJob);

        Set<String> extraDisplay = extra.stream()
                .map(skillDictionaryService::getDisplayName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        int score = expandedJob.isEmpty() ? 0
                : (matched.size() * 100) / expandedJob.size();

        MatchLevel level = score >= 80 ? MatchLevel.HIGH
                : score >= 50 ? MatchLevel.MEDIUM
                : MatchLevel.LOW;

        Map<String, String> recommendations = new LinkedHashMap<>();
        for (String skill : missing) {
            String name = skillDictionaryService.getDisplayName(skill);
            recommendations.put(name, "Learn " + name);
        }

        return AnalysisResult.builder()
                .score(score)
                .matchLevel(level)
                .matchedSkills(matchedDisplay)
                .missingSkills(missingDisplay)
                .missingSkillsWithImportance(missingWithImportance)
                .candidateExtraSkills(extraDisplay)
                .recommendations(recommendations)
                .build();
    }

    private Set<String> expandSkills(Set<String> skills) {
        return skillDictionaryService.expandSkills(skills);
    }

    public Set<String> extractSkillsFromJob(String requirements) {
        if (requirements == null || requirements.isBlank()) return Collections.emptySet();
        return skillDictionaryService.findSkillKeysInText(requirements);
    }

    public Set<String> extractSkillsFromResume(String skillsStr) {
        if (skillsStr == null || skillsStr.isBlank()) return Collections.emptySet();

        Set<String> result = new HashSet<>();
        String lower = skillsStr.toLowerCase();

        for (Map.Entry<String, SkillDictionaryService.SkillEntry> entry :
                skillDictionaryService.getEntries().entrySet()) {
            for (String synonym : entry.getValue().getSynonyms()) {
                if (lower.contains(synonym)) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    @Data
    @Builder
    public static class AnalysisResult {
        private int score;
        private MatchLevel matchLevel;
        private Set<String> matchedSkills;
        private Set<String> missingSkills;
        private List<MissingSkillInfo> missingSkillsWithImportance;
        private Set<String> candidateExtraSkills;
        private Map<String, String> recommendations;
    }

    @Data
    @Builder
    public static class MissingSkillInfo {
        private String skill;
        private String importance;
    }
}
