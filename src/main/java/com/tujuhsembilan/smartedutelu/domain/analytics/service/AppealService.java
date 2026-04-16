package com.tujuhsembilan.smartedutelu.domain.analytics.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.request.CreateAppealRequest;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.request.ResolveAppealRequest;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.AppealResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamAppeal;
import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import com.tujuhsembilan.smartedutelu.domain.analytics.enums.AppealStatus;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamAppealRepository;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamResultRepository;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppealService {

    private final ExamAppealRepository appealRepository;
    private final ExamResultRepository resultRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AppealResponse> listAppeals(String status, Pageable pageable) {
        if (status != null) {
            AppealStatus appealStatus = AppealStatus.fromString(status);
            return appealRepository.findByStatus(appealStatus, pageable).map(AppealResponse::from);
        }
        return appealRepository.findAllWithRelations(pageable).map(AppealResponse::from);
    }

    @Transactional
    public AppealResponse createAppeal(CreateAppealRequest request) {
        ExamResult result = resultRepository.findById(request.getResultId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_RES_001));

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        if (!result.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.SE_CMN_004, "Anda hanya bisa mengajukan banding untuk hasil ujian Anda sendiri");
        }

        ExamAppeal appeal = ExamAppeal.builder()
                .result(result)
                .user(user)
                .reason(request.getReason())
                .build();

        ExamAppeal saved = appealRepository.save(appeal);
        log.info("Appeal created: {} for result: {} by {}", saved.getId(), result.getId(), user.getEmail());
        return AppealResponse.from(saved);
    }

    @Transactional
    public AppealResponse resolveAppeal(UUID appealId, ResolveAppealRequest request) {
        ExamAppeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_RES_003));

        if (appeal.getStatus() == AppealStatus.APPROVED || appeal.getStatus() == AppealStatus.REJECTED) {
            throw new BusinessException(ErrorCode.SE_RES_004);
        }

        String currentEmail = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        User resolver = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));

        AppealStatus newStatus = AppealStatus.fromString(request.getStatus());
        appeal.setStatus(newStatus);
        appeal.setResolution(request.getResolution());
        appeal.setResolvedBy(resolver);
        appeal.setResolvedAt(OffsetDateTime.now());

        appealRepository.save(appeal);
        log.info("Appeal {} resolved with status: {} by {}", appealId, request.getStatus(), resolver.getEmail());
        return AppealResponse.from(appeal);
    }
}
