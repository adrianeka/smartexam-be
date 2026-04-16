package com.tujuhsembilan.smartedutelu.domain.certificate.dto.response;

import com.tujuhsembilan.smartedutelu.domain.certificate.entity.Certificate;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CertificateResponse {

    private UUID id;
    private UUID userId;
    private UUID examId;
    private UUID templateId;
    private String certificateNumber;
    private String certificateUrl;
    private Map<String, Object> metadata;
    private OffsetDateTime issuedAt;

    public static CertificateResponse from(Certificate c) {
        return CertificateResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .examId(c.getExam().getId())
                .templateId(c.getTemplate().getId())
                .certificateNumber(c.getCertificateNumber())
                .certificateUrl(c.getCertificateUrl())
                .metadata(c.getMetadata())
                .issuedAt(c.getIssuedAt())
                .build();
    }
}
