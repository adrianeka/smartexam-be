package com.tujuhsembilan.smartedutelu.domain.communication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateAnnouncementRequest {

    @NotNull
    private UUID tenantId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private Boolean emailSent;
}
