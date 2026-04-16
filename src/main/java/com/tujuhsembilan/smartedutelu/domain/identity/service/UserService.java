package com.tujuhsembilan.smartedutelu.domain.identity.service;

import com.tujuhsembilan.smartedutelu.common.dto.PageResponse;
import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.DuplicateResourceException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.AssignRolesRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.CreateUserRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.request.UpdateUserRequest;
import com.tujuhsembilan.smartedutelu.domain.identity.dto.response.UserResponse;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.Role;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.UserRole;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.RoleRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(String search, String status, String role, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecification.search(search))
                .and(UserSpecification.hasStatus(status))
                .and(UserSpecification.hasRole(role));

        Page<UserResponse> page = userRepository.findAll(spec, pageable)
                .map(this::toUserResponse);

        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            assignRolesByName(user, request.getRoles());
        } else {
            assignRolesByName(user, List.of("student"));
        }

        user = userRepository.findByIdWithRoles(user.getId()).orElseThrow();
        log.info("User dibuat oleh admin: {} ({})", user.getEmail(), user.getId());
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getPicture() != null) user.setPicture(request.getPicture());
        if (request.getLocale() != null) user.setLocale(request.getLocale());
        if (request.getTimezone() != null) user.setTimezone(request.getTimezone());

        user = userRepository.save(user);
        log.info("User diupdate oleh admin: {}", user.getId());
        return toUserResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User dihapus (soft delete): {}", id);
    }

    @Transactional
    public UserResponse updateStatus(UUID id, UpdateStatusRequest request) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setStatus(request.getStatus());
        user = userRepository.save(user);
        log.info("Status user {} diubah ke: {}", id, request.getStatus());
        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<String> getUserRoles(UUID userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();
    }

    @Transactional
    public UserResponse assignRoles(UUID userId, AssignRolesRequest request) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new BusinessException(ErrorCode.SE_CMN_002, "Satu atau lebih role tidak ditemukan");
        }

        user.getUserRoles().clear();
        for (Role role : roles) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            user.getUserRoles().add(userRole);
        }

        user = userRepository.save(user);
        log.info("Role user {} diubah: {}", userId, roles.stream().map(Role::getName).toList());
        return toUserResponse(user);
    }

    private void assignRolesByName(User user, List<String> roleNames) {
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SE_CMN_002,
                            "Role '" + roleName + "' tidak ditemukan"));
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            user.getUserRoles().add(userRole);
        }
        userRepository.save(user);
    }

    private UserResponse toUserResponse(User user) {
        List<String> roles = (user.getUserRoles() != null)
                ? user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getName())
                    .toList()
                : List.of();

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .picture(user.getPicture())
                .locale(user.getLocale())
                .timezone(user.getTimezone())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
