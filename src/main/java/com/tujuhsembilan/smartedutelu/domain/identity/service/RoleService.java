package com.tujuhsembilan.smartedutelu.domain.identity.service;

import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
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
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    // ── Read ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> getAllRoles(Pageable pageable) {
        return PageResponse.of(
                roleRepository.findAllWithPermissions(pageable)
                        .map(this::toRoleResponse)
        );
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

        long usageCount = userRoleRepository.countByRoleId(id);
        if (usageCount > 0) {
            throw new BusinessException(ErrorCode.SE_ROL_003,
                    "Role '" + role.getName() + "' masih digunakan oleh " + usageCount + " user");
        }

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

        // B12: Diff-based approach instead of delete-then-insert
        List<RolePermission> existing = rolePermissionRepository.findByRoleId(roleId);
        Set<UUID> requestedIds = request.getPermissionIds().stream().collect(Collectors.toSet());
        Set<UUID> existingIds = existing.stream()
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());

        // Remove permissions no longer requested
        List<RolePermission> toDelete = existing.stream()
                .filter(rp -> !requestedIds.contains(rp.getPermission().getId()))
                .toList();
        if (!toDelete.isEmpty()) {
            rolePermissionRepository.deleteAll(toDelete);
        }

        // Add newly requested permissions
        List<RolePermission> toAdd = permissions.stream()
                .filter(p -> !existingIds.contains(p.getId()))
                .map(p -> RolePermission.builder()
                        .role(role)
                        .permission(p)
                        .build())
                .toList();
        if (!toAdd.isEmpty()) {
            rolePermissionRepository.saveAll(toAdd);
        }

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
