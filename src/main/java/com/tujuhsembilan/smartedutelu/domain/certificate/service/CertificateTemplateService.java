package com.tujuhsembilan.smartedutelu.domain.certificate.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.CreateTemplateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.UpdateTemplateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.response.TemplateResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.entity.CertificateTemplate;
import com.tujuhsembilan.smartedutelu.domain.certificate.repository.CertificateTemplateRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
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
public class CertificateTemplateService {

    private final CertificateTemplateRepository templateRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<TemplateResponse> listTemplates(Pageable pageable) {
        return templateRepository.findAll(pageable).map(TemplateResponse::from);
    }

    @Transactional(readOnly = true)
    public TemplateResponse getTemplate(UUID id) {
        CertificateTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_001));
        return TemplateResponse.from(template);
    }

    @Transactional
    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        validateBackgroundUrl(request.getBackgroundUrl());

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User creator = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        CertificateTemplate template = CertificateTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .orientation(request.getOrientation() != null ? request.getOrientation() : "landscape")
                .backgroundUrl(request.getBackgroundUrl())
                .fields(sanitizeFields(request.getFields()))
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .createdBy(creator)
                .build();

        CertificateTemplate saved = templateRepository.save(template);
        log.info("Certificate template created: {}", saved.getId());
        return TemplateResponse.from(saved);
    }

    @Transactional
    public TemplateResponse updateTemplate(UUID id, UpdateTemplateRequest request) {
        CertificateTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_001));

        if (request.getName() != null) template.setName(request.getName());
        if (request.getDescription() != null) template.setDescription(request.getDescription());
        if (request.getOrientation() != null) template.setOrientation(request.getOrientation());
        if (request.getBackgroundUrl() != null) {
            validateBackgroundUrl(request.getBackgroundUrl());
            template.setBackgroundUrl(request.getBackgroundUrl());
        }
        if (request.getFields() != null) template.setFields(sanitizeFields(request.getFields()));
        if (request.getIsDefault() != null) template.setIsDefault(request.getIsDefault());

        return TemplateResponse.from(templateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        CertificateTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_001));
        templateRepository.delete(template);
        log.info("Certificate template deleted: {}", id);
    }

    private void validateBackgroundUrl(String url) {
        if (url != null && !url.isBlank()) {
            if (!url.startsWith("https://")) {
                throw new BusinessException(ErrorCode.SE_CMN_006, "Background URL harus menggunakan https://");
            }
        }
    }

    private java.util.Map<String, Object> sanitizeFields(java.util.Map<String, Object> fields) {
        if (fields == null) return null;
        java.util.Map<String, Object> sanitized = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey().replaceAll("[<>\"'&]", "");
            Object value = entry.getValue();
            if (value instanceof String s) {
                value = s.replaceAll("[<>\"'&]", "");
            }
            sanitized.put(key, value);
        }
        return sanitized;
    }
}
