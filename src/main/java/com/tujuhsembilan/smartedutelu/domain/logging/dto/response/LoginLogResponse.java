package com.tujuhsembilan.smartedutelu.domain.logging.dto.response;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.LoginLog;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class LoginLogResponse {

    private UUID id;
    private UUID userId;
    private String ipAddress;
    private String userAgent;
    private String device;
    private OffsetDateTime loginAt;
    private OffsetDateTime logoutAt;

    public static LoginLogResponse from(LoginLog l) {
        return LoginLogResponse.builder()
                .id(l.getId())
                .userId(l.getUser() != null ? l.getUser().getId() : null)
                .ipAddress(l.getIpAddress())
                .userAgent(l.getUserAgent())
                .device(l.getDevice())
                .loginAt(l.getLoginAt())
                .logoutAt(l.getLogoutAt())
                .build();
    }
}
