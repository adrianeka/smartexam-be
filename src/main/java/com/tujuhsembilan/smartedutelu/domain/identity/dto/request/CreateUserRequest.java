package com.tujuhsembilan.smartedutelu.domain.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Nama wajib diisi")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, max = 100, message = "Password minimal 8 karakter")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!&*_\\-]).{8,}$",
            message = "Password harus mengandung huruf besar, huruf kecil, angka, dan karakter khusus (@#$%!&*_-)")
    private String password;

    @Size(max = 50)
    private String phone;

    private List<String> roles;
}
