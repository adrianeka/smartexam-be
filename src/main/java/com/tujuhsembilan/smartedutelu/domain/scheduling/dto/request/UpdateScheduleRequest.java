package com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateScheduleRequest {

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private Integer maxParticipants;

    private String location;

    private Boolean isActive;
}
