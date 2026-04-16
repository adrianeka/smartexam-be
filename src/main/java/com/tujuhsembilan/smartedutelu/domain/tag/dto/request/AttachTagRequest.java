package com.tujuhsembilan.smartedutelu.domain.tag.dto.request;

import com.tujuhsembilan.smartedutelu.domain.tag.enums.TaggableType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AttachTagRequest {

    @NotNull
    private UUID tagId;

    @NotNull
    private TaggableType taggableType;

    @NotNull
    private UUID taggableId;
}
