package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCriteriaRequest {

    private String criterion;

    private String description;

    private BigDecimal maxScore;
}
