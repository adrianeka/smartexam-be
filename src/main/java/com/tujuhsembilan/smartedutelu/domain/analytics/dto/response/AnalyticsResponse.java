package com.tujuhsembilan.smartedutelu.domain.analytics.dto.response;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAnalytics;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AnalyticsResponse {

    private UUID id;
    private UUID examId;
    private Integer totalParticipants;
    private Integer totalCompletions;
    private BigDecimal avgScore;
    private BigDecimal passRate;
    private BigDecimal difficultyIndex;
    private BigDecimal discriminationIndex;
    private OffsetDateTime calculatedAt;

    public static AnalyticsResponse from(ExamAnalytics a) {
        return AnalyticsResponse.builder()
                .id(a.getId())
                .examId(a.getExam().getId())
                .totalParticipants(a.getTotalParticipants())
                .totalCompletions(a.getTotalCompletions())
                .avgScore(a.getAvgScore())
                .passRate(a.getPassRate())
                .difficultyIndex(a.getDifficultyIndex())
                .discriminationIndex(a.getDiscriminationIndex())
                .calculatedAt(a.getCalculatedAt())
                .build();
    }
}
