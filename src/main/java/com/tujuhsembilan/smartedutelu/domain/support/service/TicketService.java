package com.tujuhsembilan.smartedutelu.domain.support.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.exam.repository.ExamRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.CreateTicketMessageRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.CreateTicketRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.UpdateTicketRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.response.TicketMessageResponse;
import com.tujuhsembilan.smartedutelu.domain.support.dto.response.TicketResponse;
import com.tujuhsembilan.smartedutelu.domain.support.entity.Ticket;
import com.tujuhsembilan.smartedutelu.domain.support.entity.TicketMessage;
import com.tujuhsembilan.smartedutelu.domain.support.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final TicketCategoryRepository categoryRepository;
    private final TicketPriorityRepository priorityRepository;
    private final TicketStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Transactional(readOnly = true)
    public Page<TicketResponse> listTickets(UUID userId, UUID statusId, Pageable pageable) {
        if (userId != null) {
            return ticketRepository.findByUserId(userId, pageable).map(TicketResponse::from);
        }
        if (statusId != null) {
            return ticketRepository.findByStatusId(statusId, pageable).map(TicketResponse::from);
        }
        return ticketRepository.findAll(pageable).map(TicketResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listMyTickets(Pageable pageable) {
        User user = resolveCurrentUser();
        return ticketRepository.findByUserId(user.getId(), pageable).map(TicketResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listAssignedToMe(Pageable pageable) {
        User user = resolveCurrentUser();
        return ticketRepository.findByAssignedToId(user.getId(), pageable).map(TicketResponse::from);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_001));
        return TicketResponse.from(ticket);
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        User currentUser = resolveCurrentUser();
        String code = generateTicketCode();

        Ticket ticket = Ticket.builder()
                .user(currentUser)
                .code(code)
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();

        if (request.getExamId() != null) {
            ticket.setExam(examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_EXM_001)));
        }
        if (request.getCategoryId() != null) {
            ticket.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_002)));
        }
        if (request.getPriorityId() != null) {
            ticket.setPriority(priorityRepository.findById(request.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_003)));
        }

        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse updateTicket(UUID id, UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_001));

        if (request.getCategoryId() != null) {
            ticket.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_002)));
        }
        if (request.getPriorityId() != null) {
            ticket.setPriority(priorityRepository.findById(request.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_003)));
        }
        if (request.getStatusId() != null) {
            ticket.setStatus(statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_004)));
        }
        if (request.getAssignedTo() != null) {
            ticket.setAssignedTo(userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001)));
        }

        ticket.setUpdatedAt(OffsetDateTime.now());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse closeTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_001));
        ticket.setClosedAt(OffsetDateTime.now());
        ticket.setUpdatedAt(OffsetDateTime.now());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public List<TicketMessageResponse> listMessages(UUID ticketId) {
        return messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream().map(TicketMessageResponse::from).toList();
    }

    @Transactional
    public TicketMessageResponse addMessage(UUID ticketId, CreateTicketMessageRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_TKT_001));
        User currentUser = resolveCurrentUser();

        TicketMessage message = TicketMessage.builder()
                .ticket(ticket)
                .user(currentUser)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        ticket.setUpdatedAt(OffsetDateTime.now());
        ticketRepository.save(ticket);

        return TicketMessageResponse.from(messageRepository.save(message));
    }

    private String generateTicketCode() {
        String date = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "TKT-" + date + "-" + random;
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
    }
}
