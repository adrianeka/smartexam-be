package com.tujuhsembilan.smartedutelu.domain.identity.repository;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    Optional<PasswordReset> findByTokenAndExpiredAtAfter(String token, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordReset pr WHERE pr.token = :token AND pr.expiredAt > :now")
    int deleteByTokenAndExpiredAtAfter(String token, LocalDateTime now);

    @Transactional
    void deleteByExpiredAtBefore(LocalDateTime now);

    @Transactional
    void deleteByUserId(UUID userId);
}
