package com.jobboard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class SkillGapResponse {

    private int matchScore;
    private String matchLevel;
    private Set<String> matchedSkills;
    private List<MissingSkill> missingSkills;
    private Set<String> candidateExtraSkills;
    private Map<String, String> recommendations;
    private List<String> learningPath;

    @Data
    @Builder
    public static class MissingSkill {
        private String skill;
        private String importance;
    }
}
