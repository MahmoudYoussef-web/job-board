package com.jobboard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployerDashboardResponse {

    private long openJobs;
    private long totalApplications;
    private long scheduledInterviews;
    private long hiredCount;
    private List<TopCandidate> topCandidates;

    @Data
    @Builder
    public static class TopCandidate {
        private Long applicationId;
        private Long candidateId;
        private String candidateName;
        private Long jobId;
        private String jobTitle;
        private Integer score;
        private String matchLevel;
    }
}
