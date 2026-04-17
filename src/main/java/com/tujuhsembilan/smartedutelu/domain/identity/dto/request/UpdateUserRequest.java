package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 50)
    private String phone;

    private String picture;

    // B16: locale must match BCP-47 format, e.g. "en" or "en-US"
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$",
            message = "Format locale tidak valid (contoh: en, id, en-US)")
    @Size(max = 10)
    private String locale;

    // B16: timezone must match IANA format, e.g. "Asia/Jakarta"
    @Pattern(regexp = "^[A-Za-z]+/[A-Za-z_]+(/[A-Za-z_]+)?$",
            message = "Format timezone tidak valid (contoh: Asia/Jakarta, America/New_York)")
    @Size(max = 100)
    private String timezone;
}
