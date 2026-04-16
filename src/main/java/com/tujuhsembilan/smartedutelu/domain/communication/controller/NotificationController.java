package com.tujuhsembilan.smartedutelu.domain.communication.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.UpdateChannelRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.ChannelResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.NotificationResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.service.NotificationService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notifikasi pengguna")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Daftar notifikasi saya")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(notificationService.listByCurrentUser(pageable))));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Jumlah notifikasi belum dibaca")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", notificationService.countUnread())));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Tandai notifikasi sudah dibaca")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.markAsRead(id)));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Tandai semua notifikasi sudah dibaca")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllRead() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("updated", notificationService.markAllAsRead())));
    }

    @GetMapping("/channels")
    @Operation(summary = "Preferensi channel notifikasi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChannelResponse>>> getChannels() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getChannels()));
    }

    @PutMapping("/channels")
    @Operation(summary = "Update preferensi channel notifikasi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChannelResponse>> updateChannel(@Valid @RequestBody UpdateChannelRequest request) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.updateChannel(request)));
    }
}
