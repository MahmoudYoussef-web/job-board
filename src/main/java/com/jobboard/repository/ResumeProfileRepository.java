package com.jobboard.repository;

import com.jobboard.entity.ResumeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {

    Optional<ResumeProfile> findTopByCandidateIdAndIsPrimaryTrue(Long candidateId);

    Optional<ResumeProfile> findTopByCandidateIdOrderByCreatedAtDesc(Long candidateId);
}
