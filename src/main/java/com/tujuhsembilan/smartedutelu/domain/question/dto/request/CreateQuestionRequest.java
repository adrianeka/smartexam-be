package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateQuestionRequest {

    @NotNull(message = "Tenant ID tidak boleh kosong")
    private UUID tenantId;

    private UUID categoryId;

    private UUID folderId;

    @NotBlank(message = "Teks soal tidak boleh kosong")
    private String questionText;

    private String description;

    private String explanation;

    private String type;

    private Integer points;

    private String difficultyLevel;

    private Integer timeEstimateSeconds;

    private Boolean isShared;

    private List<UUID> mediaIds;

    @Valid
    private List<OptionRequest> options;

    @Data
    public static class OptionRequest {
        @NotBlank(message = "Teks opsi tidak boleh kosong")
        private String optionText;
        private Boolean isCorrect;
        private String feedback;
    }
}
