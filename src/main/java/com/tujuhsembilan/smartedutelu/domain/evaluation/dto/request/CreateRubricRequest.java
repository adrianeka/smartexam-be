package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateRubricRequest {

    @NotNull
    private UUID questionId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Positive
    private BigDecimal maxScore;

    @Valid
    private List<CriteriaItem> criteria;

    @Data
    public static class CriteriaItem {
        @NotBlank
        private String criterion;

        private String description;

        @NotNull
        @Positive
        private BigDecimal maxScore;
    }
}
