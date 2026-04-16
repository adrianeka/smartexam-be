package com.tujuhsembilan.smartedutelu.domain.calendar.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateEventRequest {

    private String title;
    private String description;
    private String color;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Boolean allDay;
    private String repeatType;
}
