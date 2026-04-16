package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateExamRequest {

    @NotNull(message = "Tenant ID tidak boleh kosong")
    private UUID tenantId;

    private UUID categoryId;

    @NotBlank(message = "Judul ujian tidak boleh kosong")
    @Size(max = 255, message = "Judul ujian maksimal 255 karakter")
    private String title;

    @NotBlank(message = "Slug tidak boleh kosong")
    @Size(max = 255, message = "Slug maksimal 255 karakter")
    private String slug;

    private String description;

    private String examType;

    @Min(value = 1, message = "Durasi ujian minimal 1 menit")
    private Integer timeLimitMinutes;

    @Min(value = 1, message = "Jumlah percobaan minimal 1")
    private Integer maxAttempts;

    @Min(value = 0, message = "Persentase kelulusan minimal 0")
    @Max(value = 100, message = "Persentase kelulusan maksimal 100")
    private Integer passPercentage;

    private BigDecimal totalScore;

    private Boolean randomQuestions;

    private Boolean randomAnswers;

    private String showResultMode;

    private Boolean allowReview;

    private Boolean shuffleSections;

    private Boolean requireProctoring;

    private String feedbackType;

    private String instructions;
}
