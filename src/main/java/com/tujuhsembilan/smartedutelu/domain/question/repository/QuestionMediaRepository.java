package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface QuestionMediaRepository extends JpaRepository<QuestionMedia, UUID> {

    List<QuestionMedia> findByQuestionIdOrderByPositionAsc(UUID questionId);

    @Modifying
    @Transactional
    void deleteByQuestionIdAndMediaFileId(UUID questionId, UUID mediaFileId);

    boolean existsByQuestionIdAndMediaFileId(UUID questionId, UUID mediaFileId);
}
