package com.jobboard.service;

import com.jobboard.entity.ResumeProfile;
import com.jobboard.entity.User;
import com.jobboard.repository.ResumeProfileRepository;
import com.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeParserService {

    private final ResumeProfileRepository resumeProfileRepository;
    private final UserRepository userRepository;
    private final SkillDictionaryService skillDictionaryService;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\+?[0-9]{1,3}[-.\\s]?)?\\(?[0-9]{2,4}\\)?[-.\\s]?[0-9]{3,4}[-.\\s]?[0-9]{3,4}");

    private static final Pattern YEARS_PATTERN =
            Pattern.compile("(\\d+)\\s*\\+?\\s*(years?|yrs?)\\s*(of|in)?\\s*(.*?)(?:\n|$|\\.)", Pattern.CASE_INSENSITIVE);

    private static final Pattern DATE_RANGE_PATTERN =
            Pattern.compile("(\\d{4})\\s*-\\s*(\\d{4}|present|current|now)", Pattern.CASE_INSENSITIVE);

    private static final Pattern EDUCATION_PATTERN =
            Pattern.compile("(bachelor|master|phd|ph\\.d|b\\.sc|m\\.sc|b\\.a|m\\.a|doctorate|diploma|degree|bachelor's|master's)\\s*(of|in|in\\s+)?\\s*([^\\n]{2,60})?");

    private static final Pattern DEGREE_PATTERN =
            Pattern.compile("(computer science|software engineering|information technology|computer engineering|it|information systems|computer|engineering|business|accounting|medicine|law)", Pattern.CASE_INSENSITIVE);

    public String extractText(File file) {
        try (PDDocument doc = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to parse PDF file");
        }
    }

    public String extractText(String filePath) {
        return extractText(new File(filePath));
    }

    public String extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group().trim() : null;
    }

    public String extractPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group().trim() : null;
    }

    public Set<String> extractSkills(String text) {
        return skillDictionaryService.findSkillKeysInText(text);
    }

    public String extractEducation(String text) {
        List<Map<String, String>> eduList = new ArrayList<>();
        String lower = text.toLowerCase();

        Matcher matcher = EDUCATION_PATTERN.matcher(lower);
        while (matcher.find()) {
            String level = matcher.group(1);
            String field = matcher.group(3);

            if (field != null && !field.isBlank()) {
                Map<String, String> entry = new LinkedHashMap<>();
                entry.put("level", level.trim());
                entry.put("field", field.trim());
                eduList.add(entry);
            }
        }

        if (eduList.isEmpty()) return "";

        try {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < eduList.size(); i++) {
                Map<String, String> e = eduList.get(i);
                sb.append("{\"level\":\"")
                        .append(escapeJson(e.get("level")))
                        .append("\",\"field\":\"")
                        .append(escapeJson(e.get("field")))
                        .append("\"}");
                if (i < eduList.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String extractExperience(String text) {
        List<Map<String, String>> expList = new ArrayList<>();
        String lower = text.toLowerCase();

        Matcher yearsMatcher = YEARS_PATTERN.matcher(lower);
        while (yearsMatcher.find()) {
            String years = yearsMatcher.group(1);
            String field = yearsMatcher.group(4);

            if (field != null && !field.isBlank()) {
                Map<String, String> entry = new LinkedHashMap<>();
                entry.put("years", years.trim());
                entry.put("field", field.trim().split("\\n")[0].trim());
                expList.add(entry);
            }
        }

        Matcher dateMatcher = DATE_RANGE_PATTERN.matcher(lower);
        while (dateMatcher.find()) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("from", dateMatcher.group(1));
            entry.put("to", dateMatcher.group(2));
            expList.add(entry);
        }

        if (expList.isEmpty()) return "";

        try {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < expList.size(); i++) {
                Map<String, String> e = expList.get(i);
                sb.append("{");
                int fieldCount = 0;
                for (Map.Entry<String, String> field : e.entrySet()) {
                    if (fieldCount > 0) sb.append(",");
                    sb.append("\"").append(escapeJson(field.getKey())).append("\":\"")
                            .append(escapeJson(field.getValue())).append("\"");
                    fieldCount++;
                }
                sb.append("}");
                if (i < expList.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public ResumeProfile parseAndSave(Long userId, String filePath) {
        String text = extractText(filePath);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResumeProfile profile = ResumeProfile.builder()
                .candidate(user)
                .email(extractEmail(text))
                .phone(extractPhone(text))
                .skills(String.join(",", extractSkills(text)))
                .education(extractEducation(text))
                .experience(extractExperience(text))
                .rawText(text)
                .fileUrl(filePath)
                .isPrimary(true)
                .build();

        ResumeProfile saved = resumeProfileRepository.save(profile);

        log.info("Resume parsed successfully: userId={}, skills={}, hasEmail={}, hasPhone={}",
                userId, saved.getSkills(),
                saved.getEmail() != null,
                saved.getPhone() != null);

        return saved;
    }

    public ResumeProfile getLatest(Long userId) {
        return resumeProfileRepository
                .findTopByCandidateIdOrderByCreatedAtDesc(userId)
                .orElse(null);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
