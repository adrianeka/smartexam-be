package com.tujuhsembilan.smartedutelu.domain.certificate.dto.response;

import com.tujuhsembilan.smartedutelu.domain.certificate.entity.CertificateTemplate;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class TemplateResponse {

    private UUID id;
    private String name;
    private String description;
    private String orientation;
    private String backgroundUrl;
    private Map<String, Object> fields;
    private Boolean isDefault;
    private UUID createdBy;
    private OffsetDateTime createdAt;

    public static TemplateResponse from(CertificateTemplate t) {
        return TemplateResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .orientation(t.getOrientation())
                .backgroundUrl(t.getBackgroundUrl())
                .fields(t.getFields())
                .isDefault(t.getIsDefault())
                .createdBy(t.getCreatedBy().getId())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
