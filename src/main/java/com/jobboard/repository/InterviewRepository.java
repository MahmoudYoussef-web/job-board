package com.jobboard.repository;

import com.jobboard.entity.Interview;
import com.jobboard.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Page<Interview> findByCandidateId(Long candidateId, Pageable pageable);

    Page<Interview> findByInterviewerId(Long interviewerId, Pageable pageable);

    @Query("SELECT i FROM Interview i WHERE i.application.id = :applicationId")
    Page<Interview> findByApplicationId(@Param("applicationId") Long applicationId, Pageable pageable);

    long countByCandidateIdAndStatus(Long candidateId, InterviewStatus status);

    long countByInterviewerIdAndScheduledAtBetween(Long interviewerId, LocalDateTime from, LocalDateTime to);
}
