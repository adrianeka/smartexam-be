package com.tujuhsembilan.smartedutelu.domain.scheduling.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamRepository;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.CreateScheduleRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.request.UpdateScheduleRequest;
import com.tujuhsembilan.smartedutelu.domain.scheduling.dto.response.ScheduleResponse;
import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.ExamSchedule;
import com.tujuhsembilan.smartedutelu.domain.scheduling.repository.ExamScheduleRepository;
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
public class ScheduleService {

    private final ExamScheduleRepository scheduleRepository;
    private final ExamRepository examRepository;

    @Transactional(readOnly = true)
    public List<ScheduleResponse> listSchedules(UUID tenantId, UUID examId,
                                                 OffsetDateTime from, OffsetDateTime to) {
        List<ExamSchedule> schedules;
        if (examId != null) {
            if (from != null && to != null) {
                schedules = scheduleRepository.findByExamIdAndDateRange(examId, from, to);
            } else {
                schedules = scheduleRepository.findByExamIdOrderByStartTimeAsc(examId);
            }
        } else if (from != null && to != null) {
            schedules = scheduleRepository.findByTenantIdAndDateRange(tenantId, from, to);
        } else {
            schedules = scheduleRepository.findByTenantId(tenantId);
        }
        return schedules.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getById(UUID tenantId, UUID id) {
        ExamSchedule schedule = scheduleRepository.findByIdAndExamTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_001));
        return toResponse(schedule);
    }

    @Transactional
    public ScheduleResponse create(CreateScheduleRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new BusinessException(ErrorCode.SE_SCH_002);
        }
        if (!request.getStartTime().isAfter(OffsetDateTime.now())) {
            throw new BusinessException(ErrorCode.SE_SCH_007);
        }

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001));

        ExamSchedule schedule = scheduleRepository.save(ExamSchedule.builder()
                .exam(exam)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxParticipants(request.getMaxParticipants())
                .location(request.getLocation())
                .build());

        log.info("Created schedule for exam {} at {}", exam.getTitle(), schedule.getStartTime());
        return toResponse(schedule);
    }

    @Transactional
    public ScheduleResponse update(UUID tenantId, UUID id, UpdateScheduleRequest request) {
        ExamSchedule schedule = scheduleRepository.findByIdAndExamTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_001));

        // D5: validate inputs before modifying entity
        OffsetDateTime newStart = request.getStartTime() != null ? request.getStartTime() : schedule.getStartTime();
        OffsetDateTime newEnd = request.getEndTime() != null ? request.getEndTime() : schedule.getEndTime();
        if (newEnd.isBefore(newStart) || newEnd.isEqual(newStart)) {
            throw new BusinessException(ErrorCode.SE_SCH_002);
        }

        if (request.getStartTime() != null) schedule.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) schedule.setEndTime(request.getEndTime());
        if (request.getMaxParticipants() != null) schedule.setMaxParticipants(request.getMaxParticipants());
        if (request.getLocation() != null) schedule.setLocation(request.getLocation());
        if (request.getIsActive() != null) schedule.setIsActive(request.getIsActive());

        schedule = scheduleRepository.save(schedule);
        log.info("Updated schedule {}", id);
        return toResponse(schedule);
    }

    @Transactional
    public void delete(UUID tenantId, UUID id) {
        ExamSchedule schedule = scheduleRepository.findByIdAndExamTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_SCH_001));
        scheduleRepository.delete(schedule);
        log.info("Deleted schedule {}", id);
    }

    private ScheduleResponse toResponse(ExamSchedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .examId(s.getExam().getId())
                .examTitle(s.getExam().getTitle())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .maxParticipants(s.getMaxParticipants())
                .location(s.getLocation())
                .isActive(s.getIsActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
