package com.tujuhsembilan.smartedutelu.domain.media.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.media.dto.request.CreateMediaRequest;
import com.tujuhsembilan.smartedutelu.domain.media.dto.response.MediaResponse;
import com.tujuhsembilan.smartedutelu.domain.media.service.MediaService;
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
@RequestMapping("/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media Files", description = "Manajemen file media")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping
    @Operation(summary = "Daftar media berdasarkan pemilik")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<MediaResponse>>> listByOwner(
            @RequestParam UUID ownerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(mediaService.listByOwner(ownerId, pageable))));
    }

    @GetMapping("/context")
    @Operation(summary = "Daftar media berdasarkan konteks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MediaResponse>>> listByContext(
            @RequestParam String context,
            @RequestParam UUID contextId) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.listByContext(context, contextId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail media file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MediaResponse>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.getMedia(id)));
    }

    @PostMapping
    @Operation(summary = "Registrasi media file baru")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MediaResponse>> register(@Valid @RequestBody CreateMediaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.registerMedia(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus media file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}
