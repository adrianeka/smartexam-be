package com.tujuhsembilan.smartedutelu.domain.tenant.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOrganizationRequest {

    @Size(max = 255, message = "Nama organisasi maksimal 255 karakter")
    private String name;

    @Size(max = 100, message = "Tipe maksimal 100 karakter")
    private String type;

    private String description;
}
