package com.tujuhsembilan.smartedutelu.domain.logging.repository;

import com.tujuhsembilan.smartedutelu.domain.logging.entity.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoginLogRepository extends JpaRepository<LoginLog, UUID> {

    Page<LoginLog> findByUserId(UUID userId, Pageable pageable);

    Optional<LoginLog> findFirstByUserIdOrderByLoginAtDesc(UUID userId);
}
