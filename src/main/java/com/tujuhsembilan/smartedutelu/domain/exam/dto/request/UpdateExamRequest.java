package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateExamRequest {

    private UUID categoryId;

    @Size(max = 255, message = "Judul ujian maksimal 255 karakter")
    private String title;

    @Size(max = 255, message = "Slug maksimal 255 karakter")
    private String slug;

    private String description;

    private String examType;

    private Integer timeLimitMinutes;

    private Integer maxAttempts;

    private Integer passPercentage;

    private BigDecimal totalScore;

    private Boolean randomQuestions;

    private Boolean randomAnswers;

    private String showResultMode;

    private Boolean allowReview;

    private Boolean shuffleSections;

    private Boolean requireProctoring;

    private String feedbackType;

    private String instructions;
}
