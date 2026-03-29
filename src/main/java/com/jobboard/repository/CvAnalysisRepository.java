package com.jobboard.repository;

import com.jobboard.entity.CvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvAnalysisRepository extends JpaRepository<CvAnalysis, Long> {
    Optional<CvAnalysis> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}