package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import lombok.Data;

@Data
public class UpdateOptionRequest {

    private String optionText;

    private Boolean isCorrect;

    private String feedback;

    private Integer position;
}
