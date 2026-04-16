package com.tujuhsembilan.smartedutelu.domain.communication.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.CurrentUserProvider;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.UpdateChannelRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.ChannelResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.NotificationResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.entity.Notification;
import com.tujuhsembilan.smartedutelu.domain.communication.entity.NotificationChannel;
import com.tujuhsembilan.smartedutelu.domain.communication.repository.NotificationChannelRepository;
import com.tujuhsembilan.smartedutelu.domain.communication.repository.NotificationRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationChannelRepository channelRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> listByCurrentUser(Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public long countUnread() {
        User user = currentUserProvider.getCurrentUser();
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_COM_002));

        User currentUser = currentUserProvider.getCurrentUser();
        if (!notif.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda tidak memiliki akses ke notifikasi ini");
        }

        notif.setIsRead(true);
        return NotificationResponse.from(notificationRepository.save(notif));
    }

    @Transactional
    public int markAllAsRead() {
        User user = currentUserProvider.getCurrentUser();
        return notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> getChannels() {
        User user = currentUserProvider.getCurrentUser();
        return channelRepository.findByUserId(user.getId()).stream()
                .map(ChannelResponse::from).toList();
    }

    @Transactional
    public ChannelResponse updateChannel(UpdateChannelRequest request) {
        User user = currentUserProvider.getCurrentUser();
        NotificationChannel channel = channelRepository
                .findByUserIdAndChannel(user.getId(), request.getChannel())
                .orElse(NotificationChannel.builder().user(user).channel(request.getChannel()).build());

        if (request.getIsEnabled() != null) channel.setIsEnabled(request.getIsEnabled());
        if (request.getPreferences() != null) channel.setPreferences(request.getPreferences());

        return ChannelResponse.from(channelRepository.save(channel));
    }

}
