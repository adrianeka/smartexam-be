package com.tujuhsembilan.smartedutelu.domain.calendar.repository;

import com.tujuhsembilan.smartedutelu.domain.calendar.entity.CalendarEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {

    Page<CalendarEvent> findByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT e FROM CalendarEvent e WHERE e.tenant.id = :tenantId AND e.startDate >= :from AND e.endDate <= :to ORDER BY e.startDate ASC")
    List<CalendarEvent> findByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                                    @Param("from") OffsetDateTime from,
                                                    @Param("to") OffsetDateTime to);
}
