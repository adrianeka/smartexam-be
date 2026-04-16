package com.tujuhsembilan.smartedutelu.domain.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamResponse {

    private UUID id;
    private UUID tenantId;
    private UUID categoryId;
    private String categoryName;
    private String title;
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
    private String status;
    private UUID createdById;
    private String createdByName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<SectionResponse> sections;
}
