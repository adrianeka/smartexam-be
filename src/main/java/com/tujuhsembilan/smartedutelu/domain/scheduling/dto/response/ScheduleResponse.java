package com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponse {

    private UUID id;
    private UUID examId;
    private String examTitle;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer maxParticipants;
    private String location;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
