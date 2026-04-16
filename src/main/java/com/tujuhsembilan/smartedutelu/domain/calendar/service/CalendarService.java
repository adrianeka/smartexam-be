package com.tujuhsembilan.smartedutelu.domain.calendar.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.request.CreateEventRequest;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.request.UpdateEventRequest;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.response.EventResponse;
import com.tujuhsembilan.smartedutelu.domain.calendar.entity.CalendarEvent;
import com.tujuhsembilan.smartedutelu.domain.calendar.repository.CalendarEventRepository;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantRepository;
import com.tujuhsembilan.smartedutelu.domain.tenant.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarEventRepository eventRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<EventResponse> listByTenant(UUID tenantId, Pageable pageable) {
        return eventRepository.findByTenantId(tenantId, pageable).map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> listByTenantAndDateRange(UUID tenantId, OffsetDateTime from, OffsetDateTime to) {
        return eventRepository.findByTenantIdAndDateRange(tenantId, from, to)
                .stream().map(EventResponse::from).toList();
    }

    // H3: Add tenant + ownership check
    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID id) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAL_001));

        if (!SecurityUtils.hasCurrentRole("ADMIN")) {
            User currentUser = resolveCurrentUser();
            boolean isOwner = event.getUser() != null && event.getUser().getId().equals(currentUser.getId());
            boolean isTenantMember = tenantUserRepository.existsByTenantIdAndUserId(
                    event.getTenant().getId(), currentUser.getId());
            if (!isOwner && !isTenantMember) {
                throw new BusinessException(ErrorCode.SE_CMN_004, "Tidak memiliki akses ke event ini");
            }
        }

        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        User currentUser = resolveCurrentUser();

        // H4: Validate dates
        validateEventDates(request.getStartDate(), request.getEndDate());

        CalendarEvent event = CalendarEvent.builder()
                .tenant(tenantRepository.findById(request.getTenantId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TNT_001)))
                .user(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .color(request.getColor())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .allDay(request.getAllDay() != null ? request.getAllDay() : false)
                .repeatType(request.getRepeatType() != null ? request.getRepeatType() : "none")
                .build();

        if (request.getExamId() != null) {
            event.setExam(examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001)));
        }

        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(UUID id, UpdateEventRequest request) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAL_001));

        // H4: Validate dates on update
        OffsetDateTime startDate = request.getStartDate() != null ? request.getStartDate() : event.getStartDate();
        OffsetDateTime endDate = request.getEndDate() != null ? request.getEndDate() : event.getEndDate();
        validateEventDates(startDate, endDate);

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getColor() != null) event.setColor(request.getColor());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getAllDay() != null) event.setAllDay(request.getAllDay());
        if (request.getRepeatType() != null) event.setRepeatType(request.getRepeatType());

        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(UUID id) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_CAL_001));
        eventRepository.delete(event);
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
    }

    private void validateEventDates(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new BusinessException(ErrorCode.SE_CMN_006, "Tanggal mulai harus sebelum tanggal selesai");
        }
    }
}
