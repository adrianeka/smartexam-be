package com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    private UUID id;
    private UUID examId;
    private String examTitle;
    private UUID scheduleId;
    private UUID roomId;
    private UUID studentId;
    private String studentName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String ipAddress;
    private String deviceInfo;
    private Boolean isProctored;
    private Boolean browserLockdown;
    private Boolean webcamRequired;
    private List<CheatingLogResponse> cheatingLogs;
}
