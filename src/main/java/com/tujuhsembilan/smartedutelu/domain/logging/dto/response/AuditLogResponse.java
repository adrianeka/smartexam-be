package com.tujuhsembilan.smartedutelu.domain.logging.dto.response;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.AuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {

    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> oldData;
    private Map<String, Object> newData;
    private OffsetDateTime createdAt;

    public static AuditLogResponse from(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId())
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .action(a.getAction())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .oldData(a.getOldData())
                .newData(a.getNewData())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
