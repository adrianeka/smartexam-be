package com.tujuhsembilan.smartedutelu.domain.scheduling.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.LogCheatingRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.CheatingLogResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.ProctorAssignmentResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.SessionResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.CheatingLog;
import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamSession;
import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ProctorAssignment;
import com.tujuhsembilan.smartedutelu.domain.scheduling.repository.CheatingLogRepository;
import com.tujuhsembilan.smartedutelu.domain.scheduling.repository.ExamSessionRepository;
import com.tujuhsembilan.smartedutelu.domain.scheduling.repository.ProctorAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProctoringService {

    private final ExamSessionRepository sessionRepository;
    private final CheatingLogRepository cheatingLogRepository;
    private final ProctorAssignmentRepository proctorAssignmentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SessionResponse> listActiveSessions(UUID tenantId) {
        return sessionRepository.findActiveSessions(tenantId).stream()
                .map(this::toSessionSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionResponse getSessionDetail(UUID sessionId) {
        ExamSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_003));
        return toSessionFull(session);
    }

    @Transactional
    public CheatingLogResponse logCheating(UUID sessionId, LogCheatingRequest request) {
        ExamSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_003));

        if (session.getEndTime() != null) {
            throw new BusinessException(ErrorCode.SE_SCH_004);
        }

        CheatingLog cheatingLog = cheatingLogRepository.save(CheatingLog.builder()
                .session(session)
                .event(request.getEvent())
                .detail(request.getDetail())
                .severity(request.getSeverity() != null ? request.getSeverity() : "low")
                .screenshotUrl(request.getScreenshotUrl())
                .build());

        log.info("Logged cheating event '{}' for session {}", request.getEvent(), sessionId);
        return toCheatingLogResponse(cheatingLog);
    }

    @Transactional
    public SessionResponse terminateSession(UUID sessionId) {
        ExamSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_003));

        if (session.getEndTime() != null) {
            throw new BusinessException(ErrorCode.SE_SCH_004);
        }

        session.setEndTime(OffsetDateTime.now());
        session = sessionRepository.save(session);
        log.info("Terminated session {}", sessionId);
        return toSessionSummary(session);
    }

    @Transactional(readOnly = true)
    public List<ProctorAssignmentResponse> getMyAssignments() {
        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User proctor = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        return proctorAssignmentRepository.findByProctorIdOrderByAssignedAtDesc(proctor.getId()).stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private SessionResponse toSessionSummary(ExamSession s) {
        return SessionResponse.builder()
                .id(s.getId())
                .examId(s.getExam().getId())
                .examTitle(s.getExam().getTitle())
                .scheduleId(s.getSchedule() != null ? s.getSchedule().getId() : null)
                .roomId(s.getRoom() != null ? s.getRoom().getId() : null)
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .ipAddress(s.getIpAddress())
                .isProctored(s.getIsProctored())
                .browserLockdown(s.getBrowserLockdown())
                .webcamRequired(s.getWebcamRequired())
                .build();
    }

    private SessionResponse toSessionFull(ExamSession s) {
        SessionResponse response = toSessionSummary(s);
        response.setDeviceInfo(s.getDeviceInfo());
        if (s.getCheatingLogs() != null) {
            response.setCheatingLogs(s.getCheatingLogs().stream()
                    .map(this::toCheatingLogResponse)
                    .toList());
        }
        return response;
    }

    private CheatingLogResponse toCheatingLogResponse(CheatingLog cl) {
        return CheatingLogResponse.builder()
                .id(cl.getId())
                .sessionId(cl.getSession().getId())
                .event(cl.getEvent())
                .detail(cl.getDetail())
                .severity(cl.getSeverity())
                .screenshotUrl(cl.getScreenshotUrl())
                .eventTime(cl.getEventTime())
                .build();
    }

    private ProctorAssignmentResponse toAssignmentResponse(ProctorAssignment pa) {
        return ProctorAssignmentResponse.builder()
                .id(pa.getId())
                .sessionId(pa.getSession().getId())
                .proctorId(pa.getProctor().getId())
                .proctorName(pa.getProctor().getName())
                .role(pa.getRole())
                .assignedAt(pa.getAssignedAt())
                .build();
    }
}
