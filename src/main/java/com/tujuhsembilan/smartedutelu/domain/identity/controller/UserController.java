package com.tujuhsembilan.smartedutelu.domain.identity.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.AssignRolesRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.CreateUserRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateUserRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.UserResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin mengelola user platform")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List semua user (paginated, filterable)")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<UserResponse> data = userService.getAllUsers(search, status, role, pageable);
        return ResponseEntity.ok(ApiResponse.success("Daftar user berhasil dimuat", data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail user berdasarkan ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    @Operation(summary = "Buat user baru (oleh admin)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User berhasil dibuat", user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update data user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User berhasil diupdate", user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Ubah status user (activate/suspend)")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        UserResponse user = userService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status user berhasil diubah", user));
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Lihat role user")
    public ResponseEntity<ApiResponse<List<String>>> getUserRoles(@PathVariable UUID id) {
        List<String> roles = userService.getUserRoles(id);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Assign/ubah role user")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRolesRequest request) {
        UserResponse user = userService.assignRoles(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role user berhasil diubah", user));
    }
}
