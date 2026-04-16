package com.tujuhsembilan.smartedutelu.domain.support.dto.response;

import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketMessage;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketMessageResponse {

    private UUID id;
    private UUID ticketId;
    private UUID userId;
    private String content;
    private String attachmentUrl;
    private OffsetDateTime createdAt;

    public static TicketMessageResponse from(TicketMessage m) {
        return TicketMessageResponse.builder()
                .id(m.getId())
                .ticketId(m.getTicket().getId())
                .userId(m.getUser().getId())
                .content(m.getContent())
                .attachmentUrl(m.getAttachmentUrl())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
