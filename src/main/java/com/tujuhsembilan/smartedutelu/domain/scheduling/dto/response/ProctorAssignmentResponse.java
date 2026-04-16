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
public class ProctorAssignmentResponse {

    private UUID id;
    private UUID sessionId;
    private UUID proctorId;
    private String proctorName;
    private String role;
    private OffsetDateTime assignedAt;
}
