package com.tujuhsembilan.smartedutelu.domain.scheduling.repository;

import com.tujuhsembilan.smartedutelu.domain.scheduling.entity.CheatingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheatingLogRepository extends JpaRepository<CheatingLog, UUID> {

    List<CheatingLog> findBySessionIdOrderByEventTimeDesc(UUID sessionId);
}
