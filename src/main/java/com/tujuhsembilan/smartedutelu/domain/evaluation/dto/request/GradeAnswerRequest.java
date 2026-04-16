package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeAnswerRequest {

    @NotNull
    @PositiveOrZero
    private BigDecimal score;

    private String feedback;
}
