package com.tujuhsembilan.smartedutelu.domain.certificate.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateTemplateRequest {

    private String name;

    private String description;

    private String orientation;

    private String backgroundUrl;

    private Map<String, Object> fields;

    private Boolean isDefault;
}
