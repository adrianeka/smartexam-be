package com.tujuhsembilan.smartedutelu.domain.tenant.repository;

import com.tujuhsembilan.smartedutelu.domain.tenant.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, UUID> {

    @Query("SELECT tu FROM TenantUser tu JOIN FETCH tu.user WHERE tu.tenant.id = :tenantId")
    List<TenantUser> findByTenantIdWithUser(UUID tenantId);

    Optional<TenantUser> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    boolean existsByTenantIdAndUserId(UUID tenantId, UUID userId);
}
