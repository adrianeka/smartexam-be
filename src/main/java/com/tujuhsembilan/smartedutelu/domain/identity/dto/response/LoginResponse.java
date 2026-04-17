package com.tujuhsembilan.smartedutelu.domain.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    /** Refresh token disimpan di HttpOnly Cookie, tidak pernah dikirim ke JSON response. */
    @JsonIgnore
    private String refreshToken;

    private UserResponse user;
}
