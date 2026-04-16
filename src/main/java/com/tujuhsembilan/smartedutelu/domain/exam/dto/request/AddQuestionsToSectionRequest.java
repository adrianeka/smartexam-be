package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class AddQuestionsToSectionRequest {

    @NotEmpty(message = "Daftar soal tidak boleh kosong")
    @Valid
    private List<QuestionItem> questions;

    @Data
    public static class QuestionItem {

        @NotNull(message = "Question ID tidak boleh kosong")
        private UUID questionId;

        private BigDecimal weight;
    }
}
