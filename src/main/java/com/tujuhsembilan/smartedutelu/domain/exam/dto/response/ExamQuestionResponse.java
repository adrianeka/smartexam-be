package com.tujuhsembilan.smartedutelu.domain.exam.dto.response;

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
public class ExamQuestionResponse {

    private UUID id;
    private UUID questionId;
    private String questionText;
    private String questionType;
    private String difficultyLevel;
    private Integer position;
    private BigDecimal weight;
}
