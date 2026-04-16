package com.tujuhsembilan.smartedutelu.domain.communication.repository;

import com.tujuhsembilan.smartedutelu.domain.communication.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, UUID> {

    List<NotificationChannel> findByUserId(UUID userId);

    Optional<NotificationChannel> findByUserIdAndChannel(UUID userId, String channel);
}
