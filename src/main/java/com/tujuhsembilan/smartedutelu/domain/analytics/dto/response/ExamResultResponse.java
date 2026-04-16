package com.tujuhsembilan.smartedutelu.domain.analytics.dto.response;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamResultResponse {

    private UUID id;
    private UUID attemptId;
    private UUID userId;
    private UUID examId;
    private BigDecimal totalScore;
    private BigDecimal maxScore;
    private BigDecimal percentage;
    private String grade;
    private Boolean isPassed;
    private BigDecimal percentileRank;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;

    public static ExamResultResponse from(ExamResult r) {
        return ExamResultResponse.builder()
                .id(r.getId())
                .attemptId(r.getAttempt().getId())
                .userId(r.getUser().getId())
                .examId(r.getExam().getId())
                .totalScore(r.getTotalScore())
                .maxScore(r.getMaxScore())
                .percentage(r.getPercentage())
                .grade(r.getGrade())
                .isPassed(r.getIsPassed())
                .percentileRank(r.getPercentileRank())
                .publishedAt(r.getPublishedAt())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
