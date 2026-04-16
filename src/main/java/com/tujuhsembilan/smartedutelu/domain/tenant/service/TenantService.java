package com.tujuhsembilan.smartedutelu.domain.tenant.service;

import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.AddMemberRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.CreateTenantRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.UpdateTenantRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.MemberResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.TenantResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.TenantUser;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final UserRepository userRepository;

    // ── Tenant CRUD ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<TenantResponse> getAllTenants(Pageable pageable) {
        return PageResponse.of(
                tenantRepository.findAll(pageable)
                        .map(this::toTenantResponse)
        );
    }

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        if (request.getDomain() != null && tenantRepository.existsByDomain(request.getDomain())) {
            throw new DuplicateResourceException(ErrorCode.SE_TNT_002);
        }

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name(request.getName())
                .domain(request.getDomain())
                .logo(request.getLogo())
                .build());

        log.info("Created tenant: {}", tenant.getName());
        return toTenantResponse(tenant);
    }

    @Transactional
    public TenantResponse updateTenant(UUID id, UpdateTenantRequest request) {
        Tenant tenant = findTenantOrThrow(id);

        if (request.getName() != null) {
            tenant.setName(request.getName());
        }
        if (request.getDomain() != null) {
            if (!request.getDomain().equals(tenant.getDomain()) && tenantRepository.existsByDomain(request.getDomain())) {
                throw new DuplicateResourceException(ErrorCode.SE_TNT_002);
            }
            tenant.setDomain(request.getDomain());
        }
        if (request.getLogo() != null) {
            tenant.setLogo(request.getLogo());
        }
        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }

        log.info("Updated tenant: {}", tenant.getName());
        return toTenantResponse(tenantRepository.save(tenant));
    }

    // ── Tenant Members ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MemberResponse> getTenantUsers(UUID tenantId) {
        findTenantOrThrow(tenantId);
        return tenantUserRepository.findByTenantIdWithUser(tenantId).stream()
                .map(this::toMemberResponse)
                .toList();
    }

    @Transactional
    public MemberResponse addTenantUser(UUID tenantId, AddMemberRequest request) {
        Tenant tenant = findTenantOrThrow(tenantId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        try {
            TenantUser tu = tenantUserRepository.save(TenantUser.builder()
                    .tenant(tenant)
                    .user(user)
                    .role(request.getRole() != null ? request.getRole() : "member")
                    .build());

            log.info("Added user {} to tenant {}", user.getEmail(), tenant.getName());
            return toMemberResponse(tu, user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ErrorCode.SE_TNT_003);
        }
    }

    @Transactional
    public void removeTenantUser(UUID tenantId, UUID userId) {
        TenantUser tu = tenantUserRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001,
                        "User is not a member of this tenant"));
        tenantUserRepository.delete(tu);
        log.info("Removed user {} from tenant {}", userId, tenantId);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    public Tenant findTenantOrThrow(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));
    }

    private TenantResponse toTenantResponse(Tenant t) {
        return TenantResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .domain(t.getDomain())
                .logo(t.getLogo())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private MemberResponse toMemberResponse(TenantUser tu) {
        User user = tu.getUser();
        return toMemberResponse(tu, user);
    }

    private MemberResponse toMemberResponse(TenantUser tu, User user) {
        return MemberResponse.builder()
                .id(tu.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .role(tu.getRole())
                .build();
    }
}
