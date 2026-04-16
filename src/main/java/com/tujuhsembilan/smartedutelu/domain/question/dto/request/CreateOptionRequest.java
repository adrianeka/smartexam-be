package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOptionRequest {

    @NotBlank(message = "Teks opsi tidak boleh kosong")
    private String optionText;

    private Boolean isCorrect;

    private String feedback;

    private Integer position;
}
