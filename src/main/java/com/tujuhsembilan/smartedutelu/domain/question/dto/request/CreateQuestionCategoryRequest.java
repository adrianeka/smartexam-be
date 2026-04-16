package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateQuestionCategoryRequest {

    @NotNull(message = "Tenant ID tidak boleh kosong")
    private UUID tenantId;

    @NotBlank(message = "Nama kategori tidak boleh kosong")
    @Size(max = 255, message = "Nama kategori maksimal 255 karakter")
    private String name;

    private String description;
}
