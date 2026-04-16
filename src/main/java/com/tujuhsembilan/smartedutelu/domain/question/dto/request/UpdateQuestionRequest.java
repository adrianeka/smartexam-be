package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateQuestionRequest {

    private UUID categoryId;

    private UUID folderId;

    private String questionText;

    private String description;

    private String explanation;

    private String type;

    private Integer points;

    private String difficultyLevel;

    private Integer timeEstimateSeconds;

    private Boolean isShared;
}
