package com.tujuhsembilan.smartedutelu.domain.tenant.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.*;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.MemberResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.OrganizationResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.TenantResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.service.OrganizationService;
import com.tujuhsembilan.smartedutelu.domain.tenant.service.TenantService;
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
@RequestMapping("/v1/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Tenants & Organizations", description = "Admin mengelola tenant, organisasi, dan keanggotaan")
public class TenantController {

    private final TenantService tenantService;
    private final OrganizationService organizationService;

    // ── 1-3: Tenant CRUD ────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Daftar semua tenant")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> listTenants() {
        List<TenantResponse> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(ApiResponse.success("Daftar tenant berhasil diambil", tenants));
    }

    @PostMapping
    @Operation(summary = "Buat tenant baru")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse tenant = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant berhasil dibuat", tenant));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request) {
        TenantResponse tenant = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant berhasil diperbarui", tenant));
    }

    // ── 4-6: Tenant Members ─────────────────────────────────────────────────────

    @GetMapping("/{id}/users")
    @Operation(summary = "Daftar anggota tenant")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> listTenantUsers(@PathVariable UUID id) {
        List<MemberResponse> members = tenantService.getTenantUsers(id);
        return ResponseEntity.ok(ApiResponse.success("Anggota tenant berhasil diambil", members));
    }

    @PostMapping("/{id}/users")
    @Operation(summary = "Tambah user ke tenant")
    public ResponseEntity<ApiResponse<MemberResponse>> addTenantUser(
            @PathVariable UUID id,
            @Valid @RequestBody AddMemberRequest request) {
        MemberResponse member = tenantService.addTenantUser(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User berhasil ditambahkan ke tenant", member));
    }

    @DeleteMapping("/{id}/users/{userId}")
    @Operation(summary = "Keluarkan user dari tenant")
    public ResponseEntity<ApiResponse<Void>> removeTenantUser(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        tenantService.removeTenantUser(id, userId);
        return ResponseEntity.ok(ApiResponse.success("User berhasil dikeluarkan dari tenant", null));
    }

    // ── 7-10: Organization CRUD ─────────────────────────────────────────────────

    @GetMapping("/{id}/organizations")
    @Operation(summary = "Daftar organisasi di tenant")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> listOrganizations(@PathVariable UUID id) {
        List<OrganizationResponse> orgs = organizationService.getOrganizationsByTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Daftar organisasi berhasil diambil", orgs));
    }

    @PostMapping("/{id}/organizations")
    @Operation(summary = "Buat organisasi di tenant")
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @PathVariable UUID id,
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse org = organizationService.createOrganization(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Organisasi berhasil dibuat", org));
    }

    @PutMapping("/{id}/organizations/{orgId}")
    @Operation(summary = "Update organisasi")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable UUID id,
            @PathVariable UUID orgId,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        OrganizationResponse org = organizationService.updateOrganization(id, orgId, request);
        return ResponseEntity.ok(ApiResponse.success("Organisasi berhasil diperbarui", org));
    }

    @DeleteMapping("/{id}/organizations/{orgId}")
    @Operation(summary = "Hapus organisasi")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @PathVariable UUID id,
            @PathVariable UUID orgId) {
        organizationService.deleteOrganization(id, orgId);
        return ResponseEntity.ok(ApiResponse.success("Organisasi berhasil dihapus", null));
    }

    // ── 11-13: Organization Members ─────────────────────────────────────────────

    @GetMapping("/{id}/organizations/{orgId}/users")
    @Operation(summary = "Daftar anggota organisasi")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> listOrgUsers(
            @PathVariable UUID id,
            @PathVariable UUID orgId) {
        List<MemberResponse> members = organizationService.getOrganizationUsers(id, orgId);
        return ResponseEntity.ok(ApiResponse.success("Anggota organisasi berhasil diambil", members));
    }

    @PostMapping("/{id}/organizations/{orgId}/users")
    @Operation(summary = "Tambah user ke organisasi")
    public ResponseEntity<ApiResponse<MemberResponse>> addOrgUser(
            @PathVariable UUID id,
            @PathVariable UUID orgId,
            @Valid @RequestBody AddMemberRequest request) {
        MemberResponse member = organizationService.addOrganizationUser(id, orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User berhasil ditambahkan ke organisasi", member));
    }

    @DeleteMapping("/{id}/organizations/{orgId}/users/{userId}")
    @Operation(summary = "Keluarkan user dari organisasi")
    public ResponseEntity<ApiResponse<Void>> removeOrgUser(
            @PathVariable UUID id,
            @PathVariable UUID orgId,
            @PathVariable UUID userId) {
        organizationService.removeOrganizationUser(id, orgId, userId);
        return ResponseEntity.ok(ApiResponse.success("User berhasil dikeluarkan dari organisasi", null));
    }
}
