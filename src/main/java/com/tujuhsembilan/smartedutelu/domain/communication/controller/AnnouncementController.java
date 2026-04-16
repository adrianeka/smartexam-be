package com.tujuhsembilan.smartedutelu.domain.communication.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.request.CreateAnnouncementRequest;
import com.tujuhsembilan.smartedutelu.domain.communication.dto.response.AnnouncementResponse;
import com.tujuhsembilan.smartedutelu.domain.communication.service.AnnouncementService;
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
@RequestMapping("/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Pengumuman")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Daftar pengumuman per tenant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> list(
            @RequestParam UUID tenantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(announcementService.listByTenant(tenantId, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail pengumuman")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Buat pengumuman baru")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@Valid @RequestBody CreateAnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(announcementService.create(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus pengumuman")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
