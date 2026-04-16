package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignPermissionsRequest {

    @NotEmpty(message = "Daftar permission tidak boleh kosong")
    private List<UUID> permissionIds;
}
