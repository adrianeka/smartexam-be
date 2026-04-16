package com.tujuhsembilan.smartedutelu.domain.logging.service;

import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.ActivityLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.AuditLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.EventResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.dto.response.LoginLogResponse;
import com.tujuhsembilan.smartedutelu.domain.logging.entity.ActivityLog;
import com.tujuhsembilan.smartedutelu.domain.logging.entity.AuditLog;
import com.tujuhsembilan.smartedutelu.domain.logging.entity.Event;
import com.tujuhsembilan.smartedutelu.domain.logging.entity.LoginLog;
import com.tujuhsembilan.smartedutelu.domain.logging.repository.ActivityLogRepository;
import com.tujuhsembilan.smartedutelu.domain.logging.repository.AuditLogRepository;
import com.tujuhsembilan.smartedutelu.domain.logging.repository.EventRepository;
import com.tujuhsembilan.smartedutelu.domain.logging.repository.LoginLogRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {

    private final EventRepository eventRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;

    // ── Events ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<EventResponse> listEvents(String eventType, UUID userId, Pageable pageable) {
        if (eventType != null) {
            return eventRepository.findByEventType(eventType, pageable).map(EventResponse::from);
        }
        if (userId != null) {
            return eventRepository.findByUserId(userId, pageable).map(EventResponse::from);
        }
        return eventRepository.findAll(pageable).map(EventResponse::from);
    }

    @Transactional
    public EventResponse logEvent(UUID userId, String eventType, String entityType, UUID entityId, Map<String, Object> metadata) {
        Event event = Event.builder()
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .metadata(metadata)
                .build();
        if (userId != null) {
            event.setUser(userRepository.getReferenceById(userId));
        }
        return EventResponse.from(eventRepository.save(event));
    }

    // ── Activity Logs ───────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> listActivityLogs(UUID userId, Pageable pageable) {
        if (userId != null) {
            return activityLogRepository.findByUserId(userId, pageable).map(ActivityLogResponse::from);
        }
        return activityLogRepository.findAll(pageable).map(ActivityLogResponse::from);
    }

    @Transactional
    public ActivityLogResponse logActivity(UUID userId, String action, String entityType, UUID entityId, Map<String, Object> metadata) {
        ActivityLog activityLog = ActivityLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .metadata(metadata)
                .build();
        if (userId != null) {
            activityLog.setUser(userRepository.getReferenceById(userId));
        }
        return ActivityLogResponse.from(activityLogRepository.save(activityLog));
    }

    // ── Audit Logs ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> listAuditLogs(String entityType, UUID entityId, Pageable pageable) {
        if (entityType != null && entityId != null) {
            return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable).map(AuditLogResponse::from);
        }
        return auditLogRepository.findAll(pageable).map(AuditLogResponse::from);
    }

    @Transactional
    public AuditLogResponse logAudit(UUID userId, String action, String entityType, UUID entityId,
                                      Map<String, Object> oldData, Map<String, Object> newData) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldData(oldData)
                .newData(newData)
                .build();
        if (userId != null) {
            auditLog.setUser(userRepository.getReferenceById(userId));
        }
        return AuditLogResponse.from(auditLogRepository.save(auditLog));
    }

    // ── Login Logs ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<LoginLogResponse> listLoginLogs(UUID userId, Pageable pageable) {
        if (userId != null) {
            return loginLogRepository.findByUserId(userId, pageable).map(LoginLogResponse::from);
        }
        return loginLogRepository.findAll(pageable).map(LoginLogResponse::from);
    }

    @Transactional
    public LoginLogResponse logLogin(UUID userId, String ipAddress, String userAgent, String device) {
        LoginLog loginLog = LoginLog.builder()
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .device(device)
                .build();
        if (userId != null) {
            loginLog.setUser(userRepository.getReferenceById(userId));
        }
        return LoginLogResponse.from(loginLogRepository.save(loginLog));
    }

    @Transactional
    public LoginLogResponse logLogout(UUID loginLogId) {
        LoginLog loginLog = loginLogRepository.findById(loginLogId)
                .orElseThrow(() -> new com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException(
                        com.tujuhsembilan.smartedutelu.common.enums.ErrorCode.SE_LOG_004));
        loginLog.setLogoutAt(java.time.OffsetDateTime.now());
        return LoginLogResponse.from(loginLogRepository.save(loginLog));
    }
}
