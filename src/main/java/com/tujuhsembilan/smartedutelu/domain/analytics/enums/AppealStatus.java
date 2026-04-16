package com.tujuhsembilan.smartedutelu.domain.analytics.enums;

public enum AppealStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static AppealStatus fromString(String value) {
        try {
            return AppealStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status banding tidak valid: " + value + ". Gunakan: PENDING, APPROVED, REJECTED");
        }
    }
}
