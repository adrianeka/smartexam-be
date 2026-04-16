package com.tujuhsembilan.smartedutelu.domain.support.dto.response;

import com.tujuhsembilan.smartedutelu.domain.support.entity.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketResponse {

    private UUID id;
    private UUID userId;
    private UUID examId;
    private String categoryName;
    private String priorityName;
    private String statusName;
    private String code;
    private String subject;
    private String message;
    private UUID assignedTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime closedAt;

    public static TicketResponse from(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .examId(t.getExam() != null ? t.getExam().getId() : null)
                .categoryName(t.getCategory() != null ? t.getCategory().getName() : null)
                .priorityName(t.getPriority() != null ? t.getPriority().getName() : null)
                .statusName(t.getStatus() != null ? t.getStatus().getName() : null)
                .code(t.getCode())
                .subject(t.getSubject())
                .message(t.getMessage())
                .assignedTo(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .closedAt(t.getClosedAt())
                .build();
    }
}
