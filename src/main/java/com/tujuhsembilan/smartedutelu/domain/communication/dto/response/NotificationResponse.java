package com.tujuhsembilan.smartedutelu.domain.communication.dto.response;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private String link;
    private OffsetDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUser().getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .link(n.getLink())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
