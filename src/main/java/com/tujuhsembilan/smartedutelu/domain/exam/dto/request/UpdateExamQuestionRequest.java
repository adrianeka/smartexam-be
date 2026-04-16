package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateExamQuestionRequest {

    private BigDecimal weight;

    private Integer position;
}
