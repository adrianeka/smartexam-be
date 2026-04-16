package com.tujuhsembilan.smartedutelu.domain.webhook.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.request.CreateWebhookRequest;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.request.UpdateWebhookRequest;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.response.WebhookLogResponse;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.response.WebhookResponse;
import com.tujuhsembilan.smartedutelu.domain.webhook.entity.Webhook;
import com.tujuhsembilan.smartedutelu.domain.webhook.repository.WebhookLogRepository;
import com.tujuhsembilan.smartedutelu.domain.webhook.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public Page<WebhookResponse> listByTenant(UUID tenantId, Pageable pageable) {
        return webhookRepository.findByTenantId(tenantId, pageable).map(WebhookResponse::from);
    }

    @Transactional(readOnly = true)
    public WebhookResponse getWebhook(UUID id) {
        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_WHK_001));
        return WebhookResponse.from(webhook);
    }

    @Transactional
    public WebhookResponse createWebhook(CreateWebhookRequest request) {
        Webhook webhook = Webhook.builder()
                .tenant(tenantRepository.findById(request.getTenantId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001)))
                .name(request.getName())
                .url(request.getUrl())
                .secret(request.getSecret())
                .events(request.getEvents())
                .build();

        return WebhookResponse.from(webhookRepository.save(webhook));
    }

    @Transactional
    public WebhookResponse updateWebhook(UUID id, UpdateWebhookRequest request) {
        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_WHK_001));

        if (request.getName() != null) webhook.setName(request.getName());
        if (request.getUrl() != null) webhook.setUrl(request.getUrl());
        if (request.getSecret() != null) webhook.setSecret(request.getSecret());
        if (request.getEvents() != null) webhook.setEvents(request.getEvents());
        if (request.getIsActive() != null) webhook.setIsActive(request.getIsActive());

        return WebhookResponse.from(webhookRepository.save(webhook));
    }

    @Transactional
    public void deleteWebhook(UUID id) {
        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_WHK_001));
        webhookRepository.delete(webhook);
    }

    @Transactional(readOnly = true)
    public Page<WebhookLogResponse> listLogs(UUID webhookId, Pageable pageable) {
        return webhookLogRepository.findByWebhookId(webhookId, pageable).map(WebhookLogResponse::from);
    }
}
