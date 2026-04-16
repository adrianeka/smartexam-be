package com.tujuhsembilan.smartedutelu.domain.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveAppealRequest {

    @NotBlank
    private String status;

    private String resolution;
}
