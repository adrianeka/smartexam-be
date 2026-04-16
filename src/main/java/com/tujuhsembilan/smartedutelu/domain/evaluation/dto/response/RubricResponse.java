package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.GradingRubric;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RubricResponse {

    private UUID id;
    private UUID questionId;
    private String title;
    private String description;
    private BigDecimal maxScore;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private List<CriteriaResponse> criteria;

    public static RubricResponse from(GradingRubric rubric) {
        return RubricResponse.builder()
                .id(rubric.getId())
                .questionId(rubric.getQuestion().getId())
                .title(rubric.getTitle())
                .description(rubric.getDescription())
                .maxScore(rubric.getMaxScore())
                .createdBy(rubric.getCreatedBy().getId())
                .createdAt(rubric.getCreatedAt())
                .criteria(rubric.getCriteria() != null
                        ? rubric.getCriteria().stream().map(CriteriaResponse::from).toList()
                        : List.of())
                .build();
    }
}
