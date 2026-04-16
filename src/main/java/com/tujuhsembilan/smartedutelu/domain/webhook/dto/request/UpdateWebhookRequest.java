package com.tujuhsembilan.smartedutelu.domain.webhook.dto.request;

import lombok.Data;

@Data
public class UpdateWebhookRequest {

    private String name;
    private String url;
    private String secret;
    private String events;
    private Boolean isActive;
}
