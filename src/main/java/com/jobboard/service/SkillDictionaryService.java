package com.jobboard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SkillDictionaryService {

    @Getter
    private Map<String, SkillEntry> entries = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var resource = new ClassPathResource("skills/skills-dictionary.json");
            entries = mapper.readValue(resource.getInputStream(),
                    new TypeReference<Map<String, SkillEntry>>() {});
            log.info("Skill dictionary loaded with {} entries", entries.size());
        } catch (IOException e) {
            log.error("Failed to load skill dictionary", e);
            entries = new HashMap<>();
        }
    }

    public String getDisplayName(String skillKey) {
        SkillEntry entry = entries.get(skillKey);
        return entry != null ? entry.getDisplayName() : skillKey;
    }

    public List<String> getSynonyms(String skillKey) {
        SkillEntry entry = entries.get(skillKey);
        return entry != null ? entry.getSynonyms() : List.of(skillKey);
    }

    public String getCategory(String skillKey) {
        SkillEntry entry = entries.get(skillKey);
        return entry != null ? entry.getCategory() : "OTHER";
    }

    public Set<String> expandSkills(Set<String> skillKeys) {
        Set<String> expanded = new HashSet<>();
        for (String key : skillKeys) {
            expanded.add(key);
            expanded.addAll(getSynonyms(key));
        }
        return expanded;
    }

    public Map<String, Set<String>> findSkillsInText(String text) {
        Map<String, Set<String>> found = new LinkedHashMap<>();
        String lower = text.toLowerCase();

        for (Map.Entry<String, SkillEntry> entry : entries.entrySet()) {
            for (String synonym : entry.getValue().getSynonyms()) {
                if (lower.contains(synonym)) {
                    found.putIfAbsent(entry.getKey(), new LinkedHashSet<>());
                    found.get(entry.getKey()).add(synonym);
                    break;
                }
            }
        }

        return found;
    }

    public Set<String> findSkillKeysInText(String text) {
        return findSkillsInText(text).keySet();
    }

    public Set<String> matchSkillsFromCommaList(String skillsStr) {
        if (skillsStr == null || skillsStr.isBlank()) return Collections.emptySet();

        Set<String> result = new HashSet<>();
        String lower = skillsStr.toLowerCase();

        for (String key : entries.keySet()) {
            SkillEntry entry = entries.get(key);
            for (String synonym : entry.getSynonyms()) {
                if (lower.contains(synonym)) {
                    result.add(key);
                    break;
                }
            }
        }
        return result;
    }

    @Getter
    public static class SkillEntry {
        private String displayName;
        private String category;
        private List<String> synonyms = new ArrayList<>();
    }
}
