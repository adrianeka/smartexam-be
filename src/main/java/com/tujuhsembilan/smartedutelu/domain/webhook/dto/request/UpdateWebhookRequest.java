package com.tujuhsembilan.smartedutelu.domain.webhook.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateWebhookRequest {

    private String name;
    /** Jika diupdate harus HTTPS dan bukan IP private. */
    private String url;
    private String secret;
    /** Jika diisi, mengganti seluruh daftar event yang di-subscribe. */
    private List<String> events;
    private Boolean isActive;
}
