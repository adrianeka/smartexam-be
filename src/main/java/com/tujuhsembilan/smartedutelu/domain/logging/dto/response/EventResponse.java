package com.tujuhsembilan.smartedutelu.domain.logging.dto.response;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.Event;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class EventResponse {

    private UUID id;
    private UUID userId;
    private String eventType;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> metadata;
    private OffsetDateTime createdAt;

    public static EventResponse from(Event e) {
        return EventResponse.builder()
                .id(e.getId())
                .userId(e.getUser() != null ? e.getUser().getId() : null)
                .eventType(e.getEventType())
                .entityType(e.getEntityType())
                .entityId(e.getEntityId())
                .metadata(e.getMetadata())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
