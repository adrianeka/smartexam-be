package com.tujuhsembilan.smartedutelu.domain.analytics.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.domain.analytics.dto.response.ExamResultResponse;
import com.tujuhsembilan.smartedutelu.domain.analytics.entity.ExamResult;
import com.tujuhsembilan.smartedutelu.domain.analytics.repository.ExamResultRepository;
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
public class ResultService {

    private final ExamResultRepository resultRepository;

    @Transactional(readOnly = true)
    public Page<ExamResultResponse> listResults(UUID examId, UUID userId, Pageable pageable) {
        if (examId != null) {
            return resultRepository.findByExamId(examId, pageable).map(ExamResultResponse::from);
        }
        if (userId != null) {
            return resultRepository.findByUserId(userId, pageable).map(ExamResultResponse::from);
        }
        return resultRepository.findAll(pageable).map(ExamResultResponse::from);
    }

    @Transactional(readOnly = true)
    public ExamResultResponse getResult(UUID id) {
        ExamResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_RES_001));
        return ExamResultResponse.from(result);
    }

    @Transactional
    public ExamResultResponse publishResult(UUID id) {
        ExamResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_RES_001));

        if (result.getPublishedAt() != null) {
            throw new BusinessException(ErrorCode.SE_RES_002);
        }

        result.setPublishedAt(OffsetDateTime.now());
        resultRepository.save(result);
        log.info("Result {} published", id);
        return ExamResultResponse.from(result);
    }
}
