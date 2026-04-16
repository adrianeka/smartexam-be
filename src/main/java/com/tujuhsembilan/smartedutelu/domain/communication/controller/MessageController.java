package com.tujuhsembilan.smartedutelu.domain.communication.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.SendMessageRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.MessageResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Pesan langsung antar pengguna")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Daftar pesan saya")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(messageService.listMyMessages(pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail pesan")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MessageResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(messageService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Kirim pesan")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MessageResponse>> send(@Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(messageService.send(request)));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Tandai pesan sudah dibaca")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MessageResponse>> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(messageService.markAsRead(id)));
    }
}
