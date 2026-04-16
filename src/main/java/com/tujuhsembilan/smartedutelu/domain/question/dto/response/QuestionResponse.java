package com.tujuhsembilan.smartedutelu.domain.question.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponse {

    private UUID id;
    private UUID tenantId;
    private UUID categoryId;
    private String categoryName;
    private UUID folderId;
    private String folderName;
    private String questionText;
    private String description;
    private String explanation;
    private String type;
    private Integer points;
    private Integer position;
    private String difficultyLevel;
    private Integer timeEstimateSeconds;
    private String picture;
    private Boolean isShared;
    private UUID createdById;
    private String createdByName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<OptionResponse> options;
    private List<AttachmentResponse> attachments;
}
