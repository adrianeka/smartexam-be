package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 255, message = "Nama maksimal 255 karakter")
    private String name;

    @Size(max = 50, message = "Nomor telepon maksimal 50 karakter")
    private String phone;

    @Size(max = 2048, message = "URL foto maksimal 2048 karakter")
    private String picture;

    @Size(max = 10, message = "Locale maksimal 10 karakter")
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "Format locale tidak valid (contoh: id, en-US)")
    private String locale;

    @Size(max = 100, message = "Timezone maksimal 100 karakter")
    @Pattern(regexp = "^[A-Za-z]+/[A-Za-z_]+(/[A-Za-z_]+)?$", message = "Format timezone tidak valid (contoh: Asia/Jakarta)")
    private String timezone;
}
