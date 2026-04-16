package com.tujuhsembilan.smartedutelu.domain.analytics.dto.response;

import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAppeal;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AppealResponse {

    private UUID id;
    private UUID resultId;
    private UUID userId;
    private String reason;
    private String status;
    private String resolution;
    private UUID resolvedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;

    public static AppealResponse from(ExamAppeal a) {
        return AppealResponse.builder()
                .id(a.getId())
                .resultId(a.getResult().getId())
                .userId(a.getUser().getId())
                .reason(a.getReason())
                .status(a.getStatus())
                .resolution(a.getResolution())
                .resolvedBy(a.getResolvedBy() != null ? a.getResolvedBy().getId() : null)
                .createdAt(a.getCreatedAt())
                .resolvedAt(a.getResolvedAt())
                .build();
    }
}
