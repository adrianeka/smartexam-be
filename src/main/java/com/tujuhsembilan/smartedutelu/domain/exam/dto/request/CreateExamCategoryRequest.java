package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateExamCategoryRequest {

    @NotNull(message = "Tenant ID tidak boleh kosong")
    private UUID tenantId;

    @NotBlank(message = "Nama kategori tidak boleh kosong")
    @Size(max = 255, message = "Nama kategori maksimal 255 karakter")
    private String name;

    @NotBlank(message = "Slug tidak boleh kosong")
    @Size(max = 255, message = "Slug maksimal 255 karakter")
    private String slug;

    private String description;

    private UUID parentId;

    private Integer position;
}
