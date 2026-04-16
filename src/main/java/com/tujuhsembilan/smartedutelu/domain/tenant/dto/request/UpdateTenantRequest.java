package com.tujuhsembilan.smartedutelu.domain.tenant.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTenantRequest {

    @Size(max = 255, message = "Nama tenant maksimal 255 karakter")
    private String name;

    @Size(max = 255, message = "Domain maksimal 255 karakter")
    private String domain;

    private String logo;

    @Size(max = 50, message = "Status maksimal 50 karakter")
    private String status;
}
