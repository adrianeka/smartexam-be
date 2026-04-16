package com.tujuhsembilan.smartedutelu.domain.question.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateFolderRequest {

    @Size(max = 255, message = "Nama folder maksimal 255 karakter")
    private String name;

    private String description;

    private UUID parentId;

    private Integer position;
}
