package com.tujuhsembilan.smartedutelu.domain.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateEventRequest {

    @NotNull
    private UUID tenantId;

    private UUID examId;

    @NotBlank
    private String title;

    private String description;

    private String color;

    @NotNull
    private OffsetDateTime startDate;

    @NotNull
    private OffsetDateTime endDate;

    private Boolean allDay;

    private String repeatType;
}
