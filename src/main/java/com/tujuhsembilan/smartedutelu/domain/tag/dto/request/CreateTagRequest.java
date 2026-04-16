package com.tujuhsembilan.smartedutelu.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private String type;
}
