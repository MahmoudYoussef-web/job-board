package com.jobboard.service;

import com.jobboard.entity.CvAnalysis;
import com.jobboard.repository.CvAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvAnalysisService {

    private final CvAnalysisRepository repository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+");

    private static final List<String> KEYWORDS =
            List.of("java", "spring", "sql", "rest", "docker", "kubernetes");

    private static final List<String> EXPERIENCE_WORDS =
            List.of("experience", "worked", "intern", "project");

    public CvAnalysis analyzeAndSave(Long userId, String filePath) {

        try (PDDocument doc = PDDocument.load(new File(filePath))) {

            String text = new PDFTextStripper().getText(doc).toLowerCase();
            int pages = doc.getNumberOfPages();

            List<String> issues = new ArrayList<>();
            int score = 100;

            // EMAIL
            Matcher matcher = EMAIL_PATTERN.matcher(text);
            String email = matcher.find() ? matcher.group() : null;

            if (email == null) {
                issues.add("Missing email");
                score -= 20;
            }

            // SKILLS
            Set<String> foundSkills = KEYWORDS.stream()
                    .filter(text::contains)
                    .collect(Collectors.toSet());

            if (foundSkills.isEmpty()) {
                issues.add("No backend skills detected");
                score -= 30;
            }

            // EXPERIENCE
            boolean hasExperience = EXPERIENCE_WORDS.stream()
                    .anyMatch(text::contains);

            if (!hasExperience) {
                issues.add("No experience or projects section");
                score -= 20;
            }

            // PAGE LIMIT
            if (pages > 2) {
                issues.add("CV exceeds 2 pages");
                score -= 10;
            }

            if (score < 0) score = 0;

            String status;
            if (score >= 80) status = "STRONG";
            else if (score >= 50) status = "GOOD";
            else status = "WEAK";

            CvAnalysis entity = CvAnalysis.builder()
                    .userId(userId)
                    .score(score)
                    .status(status)
                    .extractedSkills(String.join(",", foundSkills))
                    .issues(String.join(",", issues))
                    .build();

            CvAnalysis saved = repository.save(entity);

            log.info("CV analyzed: userId={}, score={}, status={}, skills={}, issues={}",
                    userId, score, status, foundSkills, issues);

            return saved;

        } catch (Exception e) {
            log.error("CV analysis failed for userId={}: {}", userId, e.getMessage());
            throw new RuntimeException("CV analysis failed");
        }
    }

    public CvAnalysis getLatest(Long userId) {
        return repository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);
    }

}