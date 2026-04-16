package com.tujuhsembilan.smartedutelu.domain.tenant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddMemberRequest {

    @NotNull(message = "User ID tidak boleh kosong")
    private UUID userId;

    private String role;
}
