package com.tujuhsembilan.smartedutelu.domain.webhook.dto.response;

import com.tujuhsembilan.smartedutelu.domain.webhook.entity.WebhookLog;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class WebhookLogResponse {

    private UUID id;
    private UUID webhookId;
    private String event;
    private String payload;
    private Integer responseCode;
    private String responseBody;
    private OffsetDateTime sentAt;

    public static WebhookLogResponse from(WebhookLog l) {
        return WebhookLogResponse.builder()
                .id(l.getId())
                .webhookId(l.getWebhook().getId())
                .event(l.getEvent())
                .payload(l.getPayload())
                .responseCode(l.getResponseCode())
                .responseBody(l.getResponseBody())
                .sentAt(l.getSentAt())
                .build();
    }
}
