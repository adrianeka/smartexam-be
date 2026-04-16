package com.tujuhsembilan.smartedutelu.domain.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateAppealRequest {

    @NotNull
    private UUID resultId;

    @NotBlank
    private String reason;
}
