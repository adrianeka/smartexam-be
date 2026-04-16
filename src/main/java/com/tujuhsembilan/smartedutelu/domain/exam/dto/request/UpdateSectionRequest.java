package com.tujuhsembilan.smartedutelu.domain.exam.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSectionRequest {

    @Size(max = 255, message = "Judul section maksimal 255 karakter")
    private String title;

    private String instruction;

    private Integer position;

    private Integer timeLimitSeconds;
}
