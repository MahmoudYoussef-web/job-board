package com.jobboard.specification;

import com.jobboard.dto.request.JobSearchRequest;
import com.jobboard.entity.Job;
import com.jobboard.enums.JobStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> fromFilter(JobSearchRequest filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), JobStatus.OPEN));

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }

            if (filter.getLocation() != null && !filter.getLocation().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("location")),
                        "%" + filter.getLocation().toLowerCase() + "%"));
            }

            if (filter.getSalaryMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("salaryMin"), filter.getSalaryMin()));
            }

            if (filter.getSalaryMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("salaryMax"), filter.getSalaryMax()));
            }

            if (filter.getJobType() != null) {
                predicates.add(cb.equal(root.get("jobType"), filter.getJobType()));
            }

            if (filter.getExperienceLevel() != null) {
                predicates.add(cb.equal(root.get("experienceLevel"), filter.getExperienceLevel()));
            }

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("employer", jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}