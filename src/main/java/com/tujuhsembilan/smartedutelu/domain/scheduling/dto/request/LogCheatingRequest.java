package com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogCheatingRequest {

    @NotBlank(message = "Event tidak boleh kosong")
    @Size(max = 100, message = "Event maksimal 100 karakter")
    private String event;

    private String detail;

    private String severity;

    private String screenshotUrl;
}
