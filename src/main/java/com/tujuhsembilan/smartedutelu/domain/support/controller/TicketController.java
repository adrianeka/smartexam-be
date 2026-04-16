package com.tujuhsembilan.smartedutelu.domain.support.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.CreateTicketMessageRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.CreateTicketRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.request.UpdateTicketRequest;
import com.tujuhsembilan.smartedutelu.domain.support.dto.response.TicketMessageResponse;
import com.tujuhsembilan.smartedutelu.domain.support.dto.response.TicketResponse;
import com.tujuhsembilan.smartedutelu.domain.support.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Support Tickets", description = "Manajemen tiket bantuan")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Daftar semua tiket (admin)")
    @PreAuthorize("hasAuthority('MANAGE_TICKETS')")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> list(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID statusId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(ticketService.listTickets(userId, statusId, pageable))));
    }

    @GetMapping("/my")
    @Operation(summary = "Daftar tiket saya")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> myTickets(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(ticketService.listMyTickets(pageable))));
    }

    @GetMapping("/assigned")
    @Operation(summary = "Daftar tiket yang ditugaskan ke saya")
    @PreAuthorize("hasAuthority('MANAGE_TICKETS')")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> assignedToMe(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(ticketService.listAssignedToMe(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail tiket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketResponse>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTicket(id)));
    }

    @PostMapping
    @Operation(summary = "Buat tiket baru")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketResponse>> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.createTicket(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tiket (assign, ubah status/priority)")
    @PreAuthorize("hasAuthority('MANAGE_TICKETS')")
    public ResponseEntity<ApiResponse<TicketResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdateTicketRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.updateTicket(id, request)));
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Tutup tiket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketResponse>> close(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.closeTicket(id)));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Daftar pesan dalam tiket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TicketMessageResponse>>> listMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.listMessages(id)));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Kirim pesan ke tiket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketMessageResponse>> addMessage(@PathVariable UUID id,
                                                                          @Valid @RequestBody CreateTicketMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.addMessage(id, request)));
    }
}
