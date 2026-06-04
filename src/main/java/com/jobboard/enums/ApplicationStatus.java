package com.jobboard.enums;

import java.util.Set;

public enum ApplicationStatus {

    PENDING(Set.of("SCREENING", "REJECTED")),
    SCREENING(Set.of("SHORTLISTED", "TECHNICAL_REVIEW", "REJECTED")),
    SHORTLISTED(Set.of("TECHNICAL_REVIEW", "INTERVIEW", "REJECTED")),
    TECHNICAL_REVIEW(Set.of("INTERVIEW", "HR_INTERVIEW", "REJECTED")),
    INTERVIEW(Set.of("HR_INTERVIEW", "OFFER", "REJECTED")),
    HR_INTERVIEW(Set.of("OFFER", "REJECTED")),
    OFFER(Set.of("HIRED", "REJECTED")),
    HIRED(Set.of()),
    REJECTED(Set.of()),
    WITHDRAWN(Set.of());

    private final Set<String> allowedTransitions;

    ApplicationStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(ApplicationStatus target) {
        return allowedTransitions.contains(target.name());
    }
}
