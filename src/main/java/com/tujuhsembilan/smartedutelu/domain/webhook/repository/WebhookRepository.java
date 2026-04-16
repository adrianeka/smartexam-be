package com.tujuhsembilan.smartedutelu.domain.webhook.repository;

import com.tujuhsembilan.smartedutelu.domain.webhook.entity.Webhook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {

    Page<Webhook> findByTenantId(UUID tenantId, Pageable pageable);
}
