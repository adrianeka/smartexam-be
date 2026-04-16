package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCriteriaRequest {

    @NotBlank
    private String criterion;

    private String description;

    @NotNull
    @Positive
    private BigDecimal maxScore;
}
