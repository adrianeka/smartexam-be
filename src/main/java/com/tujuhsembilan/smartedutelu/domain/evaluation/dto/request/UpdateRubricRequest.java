package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateRubricRequest {

    @NotBlank
    private String title;

    private String description;

    @Positive
    private BigDecimal maxScore;
}
