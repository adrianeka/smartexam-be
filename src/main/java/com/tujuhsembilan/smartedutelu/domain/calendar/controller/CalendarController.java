package com.tujuhsembilan.smartedutelu.domain.calendar.controller;

import com.tujuhsembilan.smartedutelu.common.dto.ApiResponse;
import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.request.CreateEventRequest;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.request.UpdateEventRequest;
import com.tujuhsembilan.smartedutelu.domain.calendar.dto.response.EventResponse;
import com.tujuhsembilan.smartedutelu.domain.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/calendar-events")
@RequiredArgsConstructor
@Tag(name = "Calendar Events", description = "Manajemen event kalender")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @Operation(summary = "Daftar event berdasarkan tenant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> list(
            @RequestParam UUID tenantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(calendarService.listByTenant(tenantId, pageable))));
    }

    @GetMapping("/range")
    @Operation(summary = "Event berdasarkan rentang tanggal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<EventResponse>>> listByRange(
            @RequestParam UUID tenantId,
            @RequestParam OffsetDateTime from,
            @RequestParam OffsetDateTime to) {
        return ResponseEntity.ok(ApiResponse.success(calendarService.listByTenantAndDateRange(tenantId, from, to)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail event")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventResponse>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(calendarService.getEvent(id)));
    }

    @PostMapping
    @Operation(summary = "Buat event baru")
    @PreAuthorize("hasAuthority('MANAGE_CALENDAR')")
    public ResponseEntity<ApiResponse<EventResponse>> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(ApiResponse.success(calendarService.createEvent(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event")
    @PreAuthorize("hasAuthority('MANAGE_CALENDAR')")
    public ResponseEntity<ApiResponse<EventResponse>> update(@PathVariable UUID id,
                                                              @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(ApiResponse.success(calendarService.updateEvent(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus event")
    @PreAuthorize("hasAuthority('MANAGE_CALENDAR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
