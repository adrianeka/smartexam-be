package com.tujuhsembilan.smartedutelu.domain.certificate.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.IssueCertificateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.response.CertificateResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.entity.Certificate;
import com.tujuhsembilan.smartedutelu.domain.certificate.entity.CertificateTemplate;
import com.tujuhsembilan.smartedutelu.domain.certificate.repository.CertificateRepository;
import com.tujuhsembilan.smartedutelu.domain.certificate.repository.CertificateTemplateRepository;
import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamResultRepository;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository resultRepository;

    @Transactional(readOnly = true)
    public Page<CertificateResponse> listCertificates(UUID userId, UUID examId, Pageable pageable) {
        if (SecurityUtils.hasCurrentRole("STUDENT")) {
            User currentUser = resolveCurrentUser();
            return certificateRepository.findByUserId(currentUser.getId(), pageable).map(CertificateResponse::from);
        }
        if (userId != null) {
            return certificateRepository.findByUserId(userId, pageable).map(CertificateResponse::from);
        }
        if (examId != null) {
            return certificateRepository.findByExamId(examId, pageable).map(CertificateResponse::from);
        }
        return certificateRepository.findAll(pageable).map(CertificateResponse::from);
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificate(UUID id) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_002));

        if (SecurityUtils.hasCurrentRole("STUDENT")) {
            User currentUser = resolveCurrentUser();
            if (!cert.getUser().getId().equals(currentUser.getId())) {
                throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses ke sertifikat ini");
            }
        }

        return CertificateResponse.from(cert);
    }

    @Transactional(readOnly = true)
    public CertificateResponse verifyCertificate(String certificateNumber) {
        Certificate cert = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_002));
        return CertificateResponse.from(cert);
    }

    @Transactional
    public CertificateResponse issueCertificate(IssueCertificateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001));

        ExamResult result = resultRepository.findByUserIdAndExamId(user.getId(), exam.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SE_RES_001, "Hasil ujian tidak ditemukan untuk siswa ini"));
        if (!Boolean.TRUE.equals(result.getIsPassed())) {
            throw new BusinessException(ErrorCode.SE_CRT_003, "Sertifikat hanya bisa diterbitkan untuk siswa yang lulus ujian");
        }

        CertificateTemplate template;
        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_001));
        } else {
            template = templateRepository.findByIsDefaultTrue()
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CRT_001));
        }

        String certNumber = generateCertificateNumber();

        Certificate certificate = Certificate.builder()
                .user(user)
                .exam(exam)
                .template(template)
                .certificateNumber(certNumber)
                .certificateUrl(request.getCertificateUrl())
                .metadata(sanitizeMap(request.getMetadata()))
                .build();

        Certificate saved = certificateRepository.save(certificate);
        log.info("Certificate issued: {} (number: {}) for user: {} exam: {}", saved.getId(), certNumber, user.getId(), exam.getId());
        return CertificateResponse.from(saved);
    }

    private String generateCertificateNumber() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String certNumber = "CERT-" + timestamp + "-" + random;

            if (!certificateRepository.existsByCertificateNumber(certNumber)) {
                return certNumber;
            }
        }
        throw new BusinessException(ErrorCode.SE_CRT_003, "Gagal generate nomor sertifikat unik setelah beberapa percobaan");
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
    }

    private Map<String, Object> sanitizeMap(Map<String, Object> input) {
        if (input == null) return null;
        Map<String, Object> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
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
