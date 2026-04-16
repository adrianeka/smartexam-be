package com.tujuhsembilan.smartedutelu.domain.webhook.dto.response;

import com.tujuhsembilan.smartedutelu.domain.webhook.entity.Webhook;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class WebhookResponse {

    private UUID id;
    private UUID tenantId;
    private String name;
    private String url;
    private List<String> events;
    private Boolean isActive;
    private OffsetDateTime createdAt;

    public static WebhookResponse from(Webhook w) {
        return WebhookResponse.builder()
                .id(w.getId())
                .tenantId(w.getTenant().getId())
                .name(w.getName())
                .url(w.getUrl())
                .events(w.getEvents() != null ? w.getEvents() : List.of())
                .isActive(w.getIsActive())
                .createdAt(w.getCreatedAt())
                .build();
    }
}
