package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.QuestionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionAttachmentRepository extends JpaRepository<QuestionAttachment, UUID> {

    Optional<QuestionAttachment> findByIdAndQuestionId(UUID id, UUID questionId);
}
