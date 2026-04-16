package com.tujuhsembilan.smartedutelu.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AttachTagRequest {

    @NotNull
    private UUID tagId;

    @NotBlank
    private String taggableType;

    @NotNull
    private UUID taggableId;
}
