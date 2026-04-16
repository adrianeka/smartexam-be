package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateExamCategoryRequest {

    @Size(max = 255, message = "Nama kategori maksimal 255 karakter")
    private String name;

    @Size(max = 255, message = "Slug maksimal 255 karakter")
    private String slug;

    private String description;

    private UUID parentId;

    private Integer position;
}
