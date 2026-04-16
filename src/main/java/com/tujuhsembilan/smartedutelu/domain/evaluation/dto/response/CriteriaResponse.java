package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.RubricCriteria;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CriteriaResponse {

    private UUID id;
    private String criterion;
    private String description;
    private BigDecimal maxScore;
    private Integer position;

    public static CriteriaResponse from(RubricCriteria criteria) {
        return CriteriaResponse.builder()
                .id(criteria.getId())
                .criterion(criteria.getCriterion())
                .description(criteria.getDescription())
                .maxScore(criteria.getMaxScore())
                .position(criteria.getPosition())
                .build();
    }
}
