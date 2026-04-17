package com.tujuhsembilan.smartedutelu.domain.identity.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.LoginResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.SessionResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.UserResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication & profile management")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;

    @Value("${application.security.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${application.security.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    // ── Cookie helper ────────────────────────────────────────────────────────

    private ResponseCookie buildRefreshCookie(String value) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/v1/auth")   // hanya dikirim ke endpoint auth
                .maxAge(Duration.ofMillis(refreshExpirationMs))
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    // ── Endpoints ────────────────────────────────────────────────────────────

    @PostMapping("/register")
    @Operation(summary = "Registrasi user baru")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registrasi berhasil", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login dan dapatkan JWT access token; refresh token disimpan di HttpOnly Cookie")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        LoginResponse response = authService.login(request, httpRequest);
        // Refresh token → HttpOnly Cookie, tidak ada di JSON response
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(response.getRefreshToken()).toString());
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout dan hapus semua sesi aktif")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication, HttpServletResponse httpResponse) {
        authService.logout(authentication.getName());
        // Hapus refresh token cookie
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
        return ResponseEntity.ok(ApiResponse.success("Logout berhasil", null));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token menggunakan refresh token dari HttpOnly Cookie")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie,
            HttpServletResponse httpResponse) {
        if (refreshTokenCookie == null || refreshTokenCookie.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("SE_AUT_002", "Refresh token tidak ditemukan", "Cookie refreshToken tidak ada"));
        }
        LoginResponse response = authService.refreshToken(refreshTokenCookie);
        // Rotate refresh token cookie
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(response.getRefreshToken()).toString());
        return ResponseEntity.ok(ApiResponse.success("Token berhasil di-refresh", response));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Kirim email reset password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Jika email terdaftar, link reset password telah dikirim", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password menggunakan token dari email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil direset", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get profil user yang sedang login")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        UserResponse user = authService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update profil sendiri")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = authService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Profil berhasil diperbarui", user));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Ganti password sendiri")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah", null));
    }

    @GetMapping("/me/sessions")
    @Operation(summary = "List sesi login aktif")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessions(Authentication authentication) {
        List<SessionResponse> sessions = authService.getSessions(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @DeleteMapping("/me/sessions/{id}")
    @Operation(summary = "Revoke sesi tertentu")
    public ResponseEntity<Void> revokeSession(
            Authentication authentication,
            @PathVariable UUID id) {
        authService.revokeSession(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
