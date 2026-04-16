package com.tujuhsembilan.smartedutelu.domain.communication.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.SendMessageRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.MessageResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.entity.Message;
import com.tujuhsembilan.smartedutelu.domain.communication.repository.MessageRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<MessageResponse> listMyMessages(Pageable pageable) {
        User user = resolveCurrentUser();
        return messageRepository.findByUserId(user.getId(), pageable).map(MessageResponse::from);
    }

    @Transactional(readOnly = true)
    public MessageResponse getById(UUID id) {
        Message msg = messageRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_COM_003));
        return MessageResponse.from(msg);
    }

    @Transactional
    public MessageResponse send(SendMessageRequest request) {
        User sender = resolveCurrentUser();
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .subject(request.getSubject())
                .content(request.getContent())
                .build();

        Message saved = messageRepository.save(message);
        log.info("Message sent: {} from {} to {}", saved.getId(), sender.getId(), receiver.getId());
        return MessageResponse.from(saved);
    }

    @Transactional
    public MessageResponse markAsRead(UUID id) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_COM_003));
        msg.setIsRead(true);
        return MessageResponse.from(messageRepository.save(msg));
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
    }
}
