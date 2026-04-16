package com.tujuhsembilan.smartedutelu.domain.logging.dto.response;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.ActivityLog;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ActivityLogResponse {

    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> metadata;
    private OffsetDateTime createdAt;

    public static ActivityLogResponse from(ActivityLog a) {
        return ActivityLogResponse.builder()
                .id(a.getId())
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .action(a.getAction())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .metadata(a.getMetadata())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
