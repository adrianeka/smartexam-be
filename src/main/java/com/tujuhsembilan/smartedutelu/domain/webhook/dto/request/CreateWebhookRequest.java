package com.tujuhsembilan.smartedutelu.domain.webhook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateWebhookRequest {

    @NotNull
    private UUID tenantId;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    private String secret;

    @NotBlank
    private String events;
}
