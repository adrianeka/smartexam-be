package com.tujuhsembilan.smartedutelu.domain.webhook.enums;

/**
 * Enum semua event yang bisa di-subscribe lewat webhook.
 * Format: domain.action
 */
public enum WebhookEvent {

    // Ujian
    EXAM_STARTED("exam.started"),
    EXAM_SUBMITTED("exam.submitted"),
    EXAM_GRADED("exam.graded"),

    // Sertifikat
    CERTIFICATE_ISSUED("certificate.issued"),

    // Tiket support
    TICKET_CREATED("ticket.created"),
    TICKET_CLOSED("ticket.closed"),
    TICKET_REPLIED("ticket.replied"),

    // User
    USER_REGISTERED("user.registered");

    private final String value;

    WebhookEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WebhookEvent fromValue(String value) {
        for (WebhookEvent e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Unknown webhook event: " + value);
    }
}
