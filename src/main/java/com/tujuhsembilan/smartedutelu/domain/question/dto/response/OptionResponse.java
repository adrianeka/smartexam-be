package com.tujuhsembilan.smartedutelu.domain.question.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionResponse {

    private UUID id;
    private String optionText;
    private Boolean isCorrect;
    private BigDecimal weight;
    private Integer position;
    private String feedback;
}
