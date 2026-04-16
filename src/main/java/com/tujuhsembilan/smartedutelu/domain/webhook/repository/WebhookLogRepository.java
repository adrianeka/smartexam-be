package com.tujuhsembilan.smartedutelu.domain.webhook.repository;

import com.tujuhsembilan.smartedutelu.domain.webhook.entity.WebhookLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookLogRepository extends JpaRepository<WebhookLog, UUID> {

    Page<WebhookLog> findByWebhookId(UUID webhookId, Pageable pageable);
}
