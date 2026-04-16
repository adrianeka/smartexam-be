package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateFolderRequest {

    @NotNull(message = "Tenant ID tidak boleh kosong")
    private UUID tenantId;

    @NotBlank(message = "Nama folder tidak boleh kosong")
    @Size(max = 255, message = "Nama folder maksimal 255 karakter")
    private String name;

    private String description;

    private UUID parentId;

    private Integer position;
}
