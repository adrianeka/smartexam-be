package com.tujuhsembilan.smartedutelu.domain.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateMediaRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String filePath;

    private String fileType;

    private Long fileSize;

    private String context;

    private UUID contextId;
}
