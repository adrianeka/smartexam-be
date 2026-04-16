package com.tujuhsembilan.smartedutelu.domain.calendar.dto.response;

import com.tujuhsembilan.smartedutelu.domain.calendar.entity.CalendarEvent;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class EventResponse {

    private UUID id;
    private UUID tenantId;
    private UUID userId;
    private UUID examId;
    private String title;
    private String description;
    private String color;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Boolean allDay;
    private String repeatType;
    private OffsetDateTime createdAt;

    public static EventResponse from(CalendarEvent e) {
        return EventResponse.builder()
                .id(e.getId())
                .tenantId(e.getTenant().getId())
                .userId(e.getUser() != null ? e.getUser().getId() : null)
                .examId(e.getExam() != null ? e.getExam().getId() : null)
                .title(e.getTitle())
                .description(e.getDescription())
                .color(e.getColor())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .allDay(e.getAllDay())
                .repeatType(e.getRepeatType())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
