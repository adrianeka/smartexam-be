package com.tujuhsembilan.smartedutelu.domain.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tujuhsembilan.smartedutelu.domain.webhook.entity.Webhook;
import com.tujuhsembilan.smartedutelu.domain.webhook.entity.WebhookLog;
import com.tujuhsembilan.smartedutelu.domain.webhook.enums.WebhookEvent;
import com.tujuhsembilan.smartedutelu.domain.webhook.repository.WebhookLogRepository;
import com.tujuhsembilan.smartedutelu.domain.webhook.repository.WebhookRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Layanan async pengiriman payload ke webhook endpoint eksternal.
 * Setiap dispatch berjalan di thread pool terpisah agar tidak memblokir
 * request utama. Tanda tangan HMAC-SHA256 dikirim via header
 * {@code X-Smartedu-Signature}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDispatcher {

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final WebhookRepository webhookRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Kirim event ke semua webhook aktif milik tenant yang subscribe ke event ini.
     *
     * @param tenantId  ID tenant pemilik webhook
     * @param event     jenis event ({@link WebhookEvent})
     * @param payload   data event yang akan dikirim sebagai JSON
     */
    @Async
    @CircuitBreaker(name = "webhook")
    public void dispatch(UUID tenantId, WebhookEvent event, Map<String, Object> payload) {
        List<Webhook> candidates = webhookRepository.findByTenantIdAndIsActiveTrue(tenantId);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(HTTP_TIMEOUT)
                .build();

        for (Webhook webhook : candidates) {
            // Cek apakah webhook subscribe ke event ini
            boolean subscribed = webhook.getEvents().stream()
                    .anyMatch(e -> e.equalsIgnoreCase(event.getValue()));
            if (!subscribed) continue;

            // SSRF check — jangan kirim ke ip private / loopback
            if (!isSafeUrl(webhook.getUrl())) {
                log.warn("Webhook {} ditolak karena URL tidak aman: {}", webhook.getId(), webhook.getUrl());
                saveLog(webhook, event.getValue(), null, -1, "URL ditolak: host private / non-HTTPS");
                continue;
            }

            sendWithRetry(client, webhook, event, payload, 2);
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void sendWithRetry(HttpClient client, Webhook webhook, WebhookEvent event,
                               Map<String, Object> payload, int maxRetries) {
        String body;
        try {
            body = objectMapper.writeValueAsString(Map.of(
                    "event", event.getValue(),
                    "webhookId", webhook.getId().toString(),
                    "data", payload
            ));
        } catch (JsonProcessingException ex) {
            log.error("Gagal serialize payload webhook {}: {}", webhook.getId(), ex.getMessage());
            return;
        }

        String signature = computeHmac(webhook.getSecret(), body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhook.getUrl()))
                .timeout(HTTP_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("X-Smartedu-Signature", "sha256=" + signature)
                .header("X-Smartedu-Event", event.getValue())
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                String responseBody = response.body();

                saveLog(webhook, event.getValue(), body, statusCode, responseBody);
                log.info("Webhook {} → {} status={}", webhook.getId(), webhook.getUrl(), statusCode);
                return; // sukses
            } catch (Exception ex) {
                if (attempt == maxRetries) {
                    log.error("Webhook {} gagal setelah {} percobaan: {}", webhook.getId(), maxRetries + 1, ex.getMessage());
                    saveLog(webhook, event.getValue(), body, -1, "Error: " + ex.getMessage());
                } else {
                    log.warn("Webhook {} percobaan {} gagal, retry...", webhook.getId(), attempt + 1);
                }
            }
        }
    }

    private void saveLog(Webhook webhook, String event, String payload, int responseCode, String responseBody) {
        try {
            WebhookLog log = WebhookLog.builder()
                    .webhook(webhook)
                    .event(event)
                    .payload(payload)
                    .responseCode(responseCode)
                    .responseBody(responseBody != null && responseBody.length() > 2000
                            ? responseBody.substring(0, 2000) : responseBody)
                    .build();
            webhookLogRepository.save(log);
        } catch (Exception ex) {
            // jangan sampai kegagalan log merusak dispatch
            log.warn("Gagal menyimpan webhook log: {}", ex.getMessage());
        }
    }

    private String computeHmac(String secret, String data) {
        if (secret == null || secret.isBlank()) return "";
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            log.warn("Gagal hitung HMAC: {}", ex.getMessage());
            return "";
        }
    }

    /**
     * Validasi URL aman: harus HTTPS dan bukan private/loopback IP.
     */
    private boolean isSafeUrl(String rawUrl) {
        try {
            URI uri = URI.create(rawUrl);
            if (!"https".equalsIgnoreCase(uri.getScheme())) return false;
            String host = uri.getHost();
            if (host == null || host.isBlank()) return false;

            InetAddress addr = InetAddress.getByName(host);
            return !addr.isLoopbackAddress()
                    && !addr.isSiteLocalAddress()
                    && !addr.isLinkLocalAddress()
                    && !addr.isAnyLocalAddress();
        } catch (Exception ex) {
            log.warn("Gagal validasi URL webhook '{}': {}", rawUrl, ex.getMessage());
            return false;
        }
    }
}
