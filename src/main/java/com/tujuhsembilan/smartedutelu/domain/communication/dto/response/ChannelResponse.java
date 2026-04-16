package com.tujuhsembilan.smartedutelu.domain.communication.dto.response;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.NotificationChannel;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ChannelResponse {

    private UUID id;
    private UUID userId;
    private String channel;
    private Boolean isEnabled;
    private Map<String, Object> preferences;

    public static ChannelResponse from(NotificationChannel c) {
        return ChannelResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .channel(c.getChannel())
                .isEnabled(c.getIsEnabled())
                .preferences(c.getPreferences())
                .build();
    }
}
