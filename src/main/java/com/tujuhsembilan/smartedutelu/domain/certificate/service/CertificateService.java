package com.tujuhsembilan.smartedutelu.domain.certificate.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.request.IssueCertificateRequest;
import com.tujuhsembilan.smartedutelu.domain.certificate.dto.response.CertificateResponse;
import com.tujuhsembilan.smartedutelu.domain.certificate.entity.Certificate;
import com.tujuhsembilan.smartedutelu.domain.certificate.entity.CertificateTemplate;
import com.tujuhsembilan.smartedutelu.domain.certificate.repository.CertificateRepository;
import com.tujuhsembilan.smartedutelu.domain.certificate.repository.CertificateTemplateRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Transactional(readOnly = true)
    public Page<CertificateResponse> listCertificates(UUID userId, UUID examId, Pageable pageable) {
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
                .metadata(request.getMetadata())
                .build();

        Certificate saved = certificateRepository.save(certificate);
        log.info("Certificate issued: {} (number: {}) for user: {} exam: {}", saved.getId(), certNumber, user.getId(), exam.getId());
        return CertificateResponse.from(saved);
    }

    private String generateCertificateNumber() {
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String certNumber = "CERT-" + timestamp + "-" + random;

        if (certificateRepository.existsByCertificateNumber(certNumber)) {
            return generateCertificateNumber();
        }
        return certNumber;
    }
}
