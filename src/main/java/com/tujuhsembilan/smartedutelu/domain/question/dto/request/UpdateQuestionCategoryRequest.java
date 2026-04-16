package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateQuestionCategoryRequest {

    @Size(max = 255, message = "Nama kategori maksimal 255 karakter")
    private String name;

    private String description;
}
