package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotBlank(message = "Status wajib diisi")
    @Pattern(regexp = "active|suspended|inactive", message = "Status harus salah satu dari: active, suspended, inactive")
    private String status;
}
