package com.tujuhsembilan.smartedutelu.domain.tenant.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.AddMemberRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.CreateOrganizationRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.request.UpdateOrganizationRequest;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.MemberResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.dto.response.OrganizationResponse;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Organization;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.OrganizationUser;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.OrganizationRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.OrganizationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final UserRepository userRepository;
    private final TenantService tenantService;

    // ── Organization CRUD ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrganizationResponse> getOrganizationsByTenant(UUID tenantId) {
        tenantService.findTenantOrThrow(tenantId);
        return organizationRepository.findByTenantId(tenantId).stream()
                .map(this::toOrgResponse)
                .toList();
    }

    @Transactional
    public OrganizationResponse createOrganization(UUID tenantId, CreateOrganizationRequest request) {
        Tenant tenant = tenantService.findTenantOrThrow(tenantId);

        if (organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)) {
            throw new DuplicateResourceException(ErrorCode.SE_ORG_002);
        }

        Organization org = organizationRepository.save(Organization.builder()
                .tenant(tenant)
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .build());

        log.info("Created organization '{}' in tenant {}", org.getName(), tenant.getName());
        return toOrgResponse(org);
    }

    @Transactional
    public OrganizationResponse updateOrganization(UUID tenantId, UUID orgId, UpdateOrganizationRequest request) {
        Organization org = findOrgOrThrow(tenantId, orgId);

        if (request.getName() != null) {
            if (!request.getName().equals(org.getName())
                    && organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)) {
                throw new DuplicateResourceException(ErrorCode.SE_ORG_002);
            }
            org.setName(request.getName());
        }
        if (request.getType() != null) {
            org.setType(request.getType());
        }
        if (request.getDescription() != null) {
            org.setDescription(request.getDescription());
        }

        log.info("Updated organization: {}", org.getName());
        return toOrgResponse(organizationRepository.save(org));
    }

    @Transactional
    public void deleteOrganization(UUID tenantId, UUID orgId) {
        Organization org = findOrgOrThrow(tenantId, orgId);
        organizationRepository.delete(org);
        log.info("Deleted organization: {}", org.getName());
    }

    // ── Organization Members ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MemberResponse> getOrganizationUsers(UUID tenantId, UUID orgId) {
        findOrgOrThrow(tenantId, orgId);
        return organizationUserRepository.findByOrganizationIdWithUser(orgId).stream()
                .map(this::toMemberResponse)
                .toList();
    }

    @Transactional
    public MemberResponse addOrganizationUser(UUID tenantId, UUID orgId, AddMemberRequest request) {
        Organization org = findOrgOrThrow(tenantId, orgId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        try {
            OrganizationUser ou = organizationUserRepository.save(OrganizationUser.builder()
                    .organization(org)
                    .user(user)
                    .role(request.getRole() != null ? request.getRole() : "member")
                    .build());

            log.info("Added user {} to organization {}", user.getEmail(), org.getName());
            return toMemberResponse(ou, user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ErrorCode.SE_ORG_003);
        }
    }

    @Transactional
    public void removeOrganizationUser(UUID tenantId, UUID orgId, UUID userId) {
        findOrgOrThrow(tenantId, orgId);
        OrganizationUser ou = organizationUserRepository.findByOrganizationIdAndUserId(orgId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001,
                        "User is not a member of this organization"));
        organizationUserRepository.delete(ou);
        log.info("Removed user {} from organization {}", userId, orgId);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private Organization findOrgOrThrow(UUID tenantId, UUID orgId) {
        tenantService.findTenantOrThrow(tenantId);
        return organizationRepository.findByIdAndTenantId(orgId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_ORG_001));
    }

    private OrganizationResponse toOrgResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .tenantId(org.getTenant().getId())
                .name(org.getName())
                .type(org.getType())
                .description(org.getDescription())
                .build();
    }

    private MemberResponse toMemberResponse(OrganizationUser ou) {
        User user = ou.getUser();
        return toMemberResponse(ou, user);
    }

    private MemberResponse toMemberResponse(OrganizationUser ou, User user) {
        return MemberResponse.builder()
                .id(ou.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .role(ou.getRole())
                .build();
    }
}
