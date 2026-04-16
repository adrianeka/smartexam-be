package com.tujuhsembilan.smartedutelu.domain.certificate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class CreateTemplateRequest {

    @NotBlank
    private String name;

    private String description;

    private String orientation;

    private String backgroundUrl;

    private Map<String, Object> fields;

    private Boolean isDefault;
}
