package com.tujuhsembilan.smartedutelu.domain.identity.repository;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    long countByRoleId(UUID roleId);

    @Query("SELECT ur FROM UserRole ur JOIN ur.role r WHERE ur.user.id = :userId AND r.name IN :roleNames")
    List<UserRole> findByUserIdAndRoleNameIn(@Param("userId") UUID userId, @Param("roleNames") List<String> roleNames);
}
