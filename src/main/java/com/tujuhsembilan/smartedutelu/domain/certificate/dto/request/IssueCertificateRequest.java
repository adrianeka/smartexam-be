package com.tujuhsembilan.smartedutelu.domain.certificate.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class IssueCertificateRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID examId;

    private UUID templateId;

    private String certificateUrl;

    private Map<String, Object> metadata;
}
