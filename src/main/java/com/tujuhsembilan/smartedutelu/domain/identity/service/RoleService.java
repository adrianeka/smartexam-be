package com.tujuhsembilan.smartedutelu.domain.identity.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.AssignPermissionsRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.CreateRoleRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateRoleRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.PermissionResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.RoleResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.Permission;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.Role;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.RolePermission;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.PermissionRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.RolePermissionRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    // ── Read ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAllWithPermissions().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ROL_001));
        return toRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByRoleId(UUID roleId) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ROL_001));

        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(rp -> toPermissionResponse(rp.getPermission()))
                .toList();
    }

    // ── Write ───────────────────────────────────────────────────────────────────

    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String normalizedName = request.getName().toLowerCase().trim();
        if (roleRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException(ErrorCode.SE_ROL_002);
        }

        Role role = roleRepository.save(Role.builder()
                .name(normalizedName)
                .description(request.getDescription())
                .build());

        log.info("Created role: {}", role.getName());
        return toRoleResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(UUID id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ROL_001));

        if (request.getName() != null) {
            String normalizedName = request.getName().toLowerCase().trim();
            if (!normalizedName.equals(role.getName()) && roleRepository.existsByName(normalizedName)) {
                throw new DuplicateResourceException(ErrorCode.SE_ROL_002);
            }
            role.setName(normalizedName);
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        log.info("Updated role: {}", role.getName());
        return toRoleResponse(roleRepository.save(role));
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ROL_001));
        roleRepository.delete(role);
        log.info("Deleted role: {}", role.getName());
    }

    @Transactional
    public List<PermissionResponse> assignPermissions(UUID roleId, AssignPermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ROL_001));

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new ResourceNotFoundException(ErrorCode.SE_PRM_001);
        }

        // Replace all existing permissions for this role
        rolePermissionRepository.deleteByRoleId(roleId);

        List<RolePermission> newMappings = permissions.stream()
                .map(p -> RolePermission.builder()
                        .role(role)
                        .permission(p)
                        .build())
                .toList();
        rolePermissionRepository.saveAll(newMappings);

        log.info("Assigned {} permissions to role: {}", permissions.size(), role.getName());
        return permissions.stream().map(this::toPermissionResponse).toList();
    }

    // ── Mapper ──────────────────────────────────────────────────────────────────

    private RoleResponse toRoleResponse(Role role) {
        List<PermissionResponse> perms = role.getRolePermissions().stream()
                .map(rp -> toPermissionResponse(rp.getPermission()))
                .toList();
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(perms)
                .build();
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
