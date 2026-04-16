package com.tujuhsembilan.smartedutelu.domain.exam.repository;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, UUID> {

    @Query("SELECT eq FROM ExamQuestion eq LEFT JOIN FETCH eq.question WHERE eq.section.id = :sectionId ORDER BY eq.position")
    List<ExamQuestion> findBySectionIdWithQuestion(UUID sectionId);

    Optional<ExamQuestion> findByIdAndSectionId(UUID id, UUID sectionId);

    boolean existsBySectionIdAndQuestionId(UUID sectionId, UUID questionId);
}
