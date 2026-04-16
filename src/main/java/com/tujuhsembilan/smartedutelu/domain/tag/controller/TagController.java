package com.tujuhsembilan.smartedutelu.domain.tag.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.request.AttachTagRequest;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.request.CreateTagRequest;
import com.tujuhsembilan.smartedutelu.domain.tag.dto.response.TagResponse;
import com.tujuhsembilan.smartedutelu.domain.tag.service.TagService;
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
@RequestMapping("/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Manajemen tag & tagging")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Daftar tag")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<TagResponse>>> list(
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(tagService.listByType(type, pageable))));
    }

    @PostMapping
    @Operation(summary = "Buat tag baru")
    @PreAuthorize("hasAuthority('MANAGE_TAGS')")
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody CreateTagRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tagService.createTag(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus tag")
    @PreAuthorize("hasAuthority('MANAGE_TAGS')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/attach")
    @Operation(summary = "Pasang tag ke entitas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> attach(@Valid @RequestBody AttachTagRequest request) {
        tagService.attachTag(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/detach")
    @Operation(summary = "Lepas tag dari entitas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> detach(@RequestParam UUID tagId,
                                        @RequestParam String taggableType,
                                        @RequestParam UUID taggableId) {
        tagService.detachTag(tagId, taggableType, taggableId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entity")
    @Operation(summary = "Daftar tag untuk entitas tertentu")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getTagsForEntity(
            @RequestParam String taggableType,
            @RequestParam UUID taggableId) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getTagsForEntity(taggableType, taggableId)));
    }
}
