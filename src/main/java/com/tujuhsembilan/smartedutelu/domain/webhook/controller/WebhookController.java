package com.tujuhsembilan.smartedutelu.domain.webhook.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.request.CreateWebhookRequest;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.request.UpdateWebhookRequest;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.response.WebhookLogResponse;
import com.tujuhsembilan.smartedutelu.domain.webhook.dto.response.WebhookResponse;
import com.tujuhsembilan.smartedutelu.domain.webhook.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Manajemen webhook integrations")
public class WebhookController {

    private final WebhookService webhookService;

    @GetMapping
    @Operation(summary = "Daftar webhook per tenant")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<ApiResponse<PageResponse<WebhookResponse>>> list(
            @RequestParam UUID tenantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(webhookService.listByTenant(tenantId, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail webhook")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<ApiResponse<WebhookResponse>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(webhookService.getWebhook(id)));
    }

    @PostMapping
    @Operation(summary = "Buat webhook baru")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<ApiResponse<WebhookResponse>> create(@Valid @RequestBody CreateWebhookRequest request) {
        return ResponseEntity.ok(ApiResponse.success(webhookService.createWebhook(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update webhook")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<ApiResponse<WebhookResponse>> update(@PathVariable UUID id,
                                                                @Valid @RequestBody UpdateWebhookRequest request) {
        return ResponseEntity.ok(ApiResponse.success(webhookService.updateWebhook(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus webhook")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        webhookService.deleteWebhook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/logs")
    @Operation(summary = "Daftar log pengiriman webhook")
    @PreAuthorize("hasAuthority('MANAGE_WEBHOOKS')")
    public ResponseEntity<ApiResponse<PageResponse<WebhookLogResponse>>> listLogs(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(webhookService.listLogs(id, pageable))));
    }
}
