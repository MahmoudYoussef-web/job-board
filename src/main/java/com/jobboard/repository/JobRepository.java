package com.jobboard.repository;

import com.jobboard.entity.Job;
import com.jobboard.enums.ExperienceLevel;
import com.jobboard.enums.JobStatus;
import com.jobboard.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * JpaSpecificationExecutor enables dynamic search queries (Phase 2 — JobSpecification).
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    // ----------------------------------------------------------------
    // Employer-scoped queries
    // ----------------------------------------------------------------

    Page<Job> findByEmployerId(Long employerId, Pageable pageable);

    Page<Job> findByEmployerIdAndStatus(Long employerId, JobStatus status, Pageable pageable);

    // ----------------------------------------------------------------
    // Public listing queries
    // ----------------------------------------------------------------

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    /**
     * Full-text style search across title and location (Phase 2 will replace with
     * a JpaSpecification or Hibernate Search for production scalability).
     */
    @Query("""
        SELECT j FROM Job j
        WHERE j.status = 'OPEN'
          AND (:keyword IS NULL
               OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:jobType IS NULL OR j.jobType = :jobType)
          AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
          AND (:salaryMin IS NULL OR j.salaryMin >= :salaryMin)
          AND (:salaryMax IS NULL OR j.salaryMax <= :salaryMax)
        """)
    Page<Job> searchJobs(
            @Param("keyword")         String keyword,
            @Param("location")        String location,
            @Param("jobType")         JobType jobType,
            @Param("experienceLevel") ExperienceLevel experienceLevel,
            @Param("salaryMin")       BigDecimal salaryMin,
            @Param("salaryMax")       BigDecimal salaryMax,
            Pageable pageable
    );

    /**
     * Fetch job with employer eagerly to avoid N+1 in detail view.
     */
    @Query("SELECT j FROM Job j JOIN FETCH j.employer WHERE j.id = :id AND j.status = 'OPEN'")
    Optional<Job> findOpenJobByIdWithEmployer(@Param("id") Long id);

    long countByEmployerIdAndStatus(Long employerId, JobStatus status);
}
