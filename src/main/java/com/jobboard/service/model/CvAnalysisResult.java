package com.jobboard.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CvAnalysisResult {
    private int score;
    private String status;
    private List<String> issues;
}