package com.tujuhsembilan.smartedutelu.domain.communication.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.CreateAnnouncementRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.AnnouncementResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.entity.Announcement;
import com.tujuhsembilan.smartedutelu.domain.communication.repository.AnnouncementRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.entity.Tenant;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final TenantUserRepository tenantUserRepository;

    @Transactional(readOnly = true)
    public Page<AnnouncementResponse> listByTenant(UUID tenantId, Pageable pageable) {
        return announcementRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable)
                .map(AnnouncementResponse::from);
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse getById(UUID id) {
        Announcement ann = announcementRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_COM_001));
        return AnnouncementResponse.from(ann);
    }

    @Transactional
    public AnnouncementResponse create(CreateAnnouncementRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001));

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User creator = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        if (!tenantUserRepository.existsByTenantIdAndUserId(tenant.getId(), creator.getId())) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses ke tenant ini");
        }

        Announcement announcement = Announcement.builder()
                .tenant(tenant)
                .title(request.getTitle())
                .message(request.getMessage())
                .createdBy(creator)
                .emailSent(request.getEmailSent() != null ? request.getEmailSent() : false)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        log.info("Announcement created: {}", saved.getId());
        return AnnouncementResponse.from(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Announcement ann = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_COM_001));

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        if (!ann.getCreatedBy().getId().equals(currentUser.getId()) && !SecurityUtils.hasCurrentRole("ADMIN")) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses untuk menghapus pengumuman ini");
        }

        if (!tenantUserRepository.existsByTenantIdAndUserId(ann.getTenant().getId(), currentUser.getId())) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses ke tenant ini");
        }

        announcementRepository.delete(ann);
        log.info("Announcement deleted: {}", id);
    }
}
