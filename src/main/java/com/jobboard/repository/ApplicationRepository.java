package com.jobboard.repository;

import com.jobboard.entity.Application;
import com.jobboard.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // ----------------------------------------------------------------
    // Duplicate-application guard (CRITICAL)
    // ----------------------------------------------------------------

    /**
     * Returns true if the candidate has already applied to the given job.
     * Backed by the UNIQUE(job_id, candidate_id) constraint.
     */
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);

    // ----------------------------------------------------------------
    // Candidate-scoped queries
    // ----------------------------------------------------------------

    Page<Application> findByCandidateId(Long candidateId, Pageable pageable);

    Page<Application> findByCandidateIdAndStatus(Long candidateId, ApplicationStatus status, Pageable pageable);

    Optional<Application> findByIdAndCandidateId(Long applicationId, Long candidateId);

    // ----------------------------------------------------------------
    // Employer-scoped queries (via job ownership)
    // ----------------------------------------------------------------

    Page<Application> findByJobId(Long jobId, Pageable pageable);

    Page<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status, Pageable pageable);

    /**
     * All applications for jobs owned by a specific employer — used in employer dashboard.
     *
     * countQuery is MANDATORY: Hibernate cannot apply firstResult/maxResults when a
     * collection fetch join is present in the main query.  Without a separate count
     * query Spring Data would issue an in-memory HibernateException at runtime.
     */
    @Query(
        value = """
            SELECT a FROM Application a
            JOIN FETCH a.job j
            JOIN FETCH a.candidate c
            WHERE j.employer.id = :employerId
            """,
        countQuery = """
            SELECT COUNT(a) FROM Application a
            JOIN a.job j
            WHERE j.employer.id = :employerId
            """
    )
    Page<Application> findByEmployerId(@Param("employerId") Long employerId, Pageable pageable);

    /**
     * Count applications by status for a job — used in job statistics.
     */
    long countByJobIdAndStatus(Long jobId, ApplicationStatus status);

    long countByCandidateId(Long candidateId);
}
