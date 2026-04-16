package com.tujuhsembilan.smartedutelu.config;

import com.tujuhsembilan.smartedutelu.domain.identity.entity.Permission;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.Role;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.RolePermission;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.UserRole;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.PermissionRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.RolePermissionRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.RoleRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.seeder.enabled:true}")
    private boolean seederEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seederEnabled) {
            log.info("DataSeeder disabled via configuration");
            return;
        }
        seedPermissions();
        seedUsers();
    }

    // ── Permissions ─────────────────────────────────────────────────────────────

    private static final List<SeedPermission> ALL_PERMISSIONS = List.of(
            new SeedPermission("user:read",       "Melihat data user"),
            new SeedPermission("user:write",      "Membuat dan mengedit user"),
            new SeedPermission("user:delete",     "Menghapus user"),
            new SeedPermission("role:read",       "Melihat data role"),
            new SeedPermission("role:write",      "Membuat dan mengedit role"),
            new SeedPermission("exam:read",       "Melihat ujian"),
            new SeedPermission("exam:write",      "Membuat dan mengedit ujian"),
            new SeedPermission("exam:delete",     "Menghapus ujian"),
            new SeedPermission("exam:publish",    "Mempublikasikan ujian"),
            new SeedPermission("question:read",   "Melihat bank soal"),
            new SeedPermission("question:write",  "Membuat dan mengedit soal"),
            new SeedPermission("question:delete", "Menghapus soal"),
            new SeedPermission("result:read",     "Melihat hasil ujian"),
            new SeedPermission("result:grade",    "Menilai/koreksi jawaban"),
            new SeedPermission("report:read",     "Melihat laporan dan analitik"),
            new SeedPermission("proctor:manage",  "Mengelola sesi proctoring")
    );

    // Permissions granted per role
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "admin",   Set.of("user:read", "user:write", "user:delete",
                              "role:read", "role:write",
                              "exam:read", "exam:write", "exam:delete", "exam:publish",
                              "question:read", "question:write", "question:delete",
                              "result:read", "result:grade",
                              "report:read", "proctor:manage"),
            "teacher", Set.of("exam:read", "exam:write", "exam:publish",
                              "question:read", "question:write",
                              "result:read", "result:grade",
                              "report:read"),
            "proctor", Set.of("exam:read", "result:read", "proctor:manage"),
            "student", Set.of("exam:read", "result:read")
    );

    private void seedPermissions() {
        // Upsert permissions
        Map<String, Permission> existing = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        for (SeedPermission sp : ALL_PERMISSIONS) {
            if (!existing.containsKey(sp.name())) {
                Permission saved = permissionRepository.save(Permission.builder()
                        .name(sp.name())
                        .description(sp.description())
                        .build());
                existing.put(saved.getName(), saved);
                log.info("Seeded permission: {}", sp.name());
            }
        }

        // Assign permissions to roles
        for (Map.Entry<String, Set<String>> entry : ROLE_PERMISSIONS.entrySet()) {
            String roleName = entry.getKey();
            Set<String> permNames = entry.getValue();

            roleRepository.findByName(roleName).ifPresent(role -> {
                long alreadyAssigned = rolePermissionRepository.findByRoleId(role.getId()).size();
                if (alreadyAssigned > 0) {
                    log.debug("Permissions already assigned to role '{}', skipping", roleName);
                    return;
                }

                List<RolePermission> mappings = permNames.stream()
                        .filter(existing::containsKey)
                        .map(name -> RolePermission.builder()
                                .role(role)
                                .permission(existing.get(name))
                                .build())
                        .toList();
                rolePermissionRepository.saveAll(mappings);
                log.info("Assigned {} permissions to role '{}'", mappings.size(), roleName);
            });
        }
    }

    // ── Users ────────────────────────────────────────────────────────────────────

    private void seedUsers() {
        List<SeedUser> seeds = List.of(
                new SeedUser("Super Admin", "admin@smartexam.com", "admin123", "admin"),
                new SeedUser("Budi Santoso", "teacher@smartexam.com", "teacher123", "teacher"),
                new SeedUser("Siti Nurhaliza", "student@smartexam.com", "student123", "student")
        );

        for (SeedUser seed : seeds) {
            if (userRepository.existsByEmail(seed.email())) {
                log.debug("Seed user already exists: {}", seed.email());
                continue;
            }

            Role role = roleRepository.findByName(seed.roleName())
                    .orElseThrow(() -> new IllegalStateException(
                            "Role '" + seed.roleName() + "' not found. Make sure V13 migration has run."));

            User user = userRepository.save(User.builder()
                    .name(seed.name())
                    .email(seed.email())
                    .passwordHash(passwordEncoder.encode(seed.password()))
                    .status("active")
                    .build());

            userRoleRepository.save(UserRole.builder()
                    .user(user)
                    .role(role)
                    .build());

            log.info("Seeded user: {} [{}]", seed.name(), seed.roleName());
        }
    }

    private record SeedPermission(String name, String description) {}
    private record SeedUser(String name, String email, String password, String roleName) {}
}

