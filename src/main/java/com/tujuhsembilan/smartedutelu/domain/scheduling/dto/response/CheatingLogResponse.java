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
public class CheatingLogResponse {

    private UUID id;
    private UUID sessionId;
    private String event;
    private String detail;
    private String severity;
    private String screenshotUrl;
    private OffsetDateTime eventTime;
}
