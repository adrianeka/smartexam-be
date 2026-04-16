package com.tujuhsembilan.smartedutelu.domain.identity.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.AssignPermissionsRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.CreateRoleRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateRoleRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.PermissionResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.RoleResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Role & Permission Management", description = "Admin mengelola role dan permission platform")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Daftar semua role")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Daftar role berhasil diambil", roles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail role berdasarkan ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable UUID id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("Role berhasil diambil", role));
    }

    @PostMapping
    @Operation(summary = "Buat role baru")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role berhasil dibuat", role));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role berhasil diperbarui", role));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus role")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role berhasil dihapus", null));
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "Daftar permission yang dimiliki role")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByRole(@PathVariable UUID id) {
        List<PermissionResponse> perms = roleService.getPermissionsByRoleId(id);
        return ResponseEntity.ok(ApiResponse.success("Permission role berhasil diambil", perms));
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "Assign permission ke role (replace semua)")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> assignPermissions(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        List<PermissionResponse> perms = roleService.assignPermissions(id, request);
        return ResponseEntity.ok(ApiResponse.success("Permission berhasil di-assign ke role", perms));
    }

    @GetMapping("/permissions")
    @Operation(summary = "Daftar semua permission yang tersedia")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> perms = roleService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("Daftar permission berhasil diambil", perms));
    }
}
