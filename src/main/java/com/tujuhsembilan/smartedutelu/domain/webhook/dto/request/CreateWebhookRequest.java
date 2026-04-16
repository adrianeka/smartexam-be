package com.tujuhsembilan.smartedutelu.domain.webhook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateWebhookRequest {

    @NotNull
    private UUID tenantId;

    @NotBlank
    private String name;

    /** Harus HTTPS dan tidak boleh mengarah ke IP private (SSRF protection). */
    @NotBlank
    private String url;

    /** Jika tidak diberikan, akan di-generate otomatis. */
    private String secret;

    /** Daftar event yang di-subscribe, misal: ["exam.graded", "certificate.issued"] */
    @NotEmpty
    private List<@NotBlank String> events;
}
