package com.tujuhsembilan.smartedutelu.domain.exam.repository;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.ExamSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSectionRepository extends JpaRepository<ExamSection, UUID> {

    List<ExamSection> findByExamIdOrderByPositionAsc(UUID examId);

    Optional<ExamSection> findByIdAndExamId(UUID id, UUID examId);

    @Query("SELECT s FROM ExamSection s LEFT JOIN FETCH s.examQuestions eq LEFT JOIN FETCH eq.question WHERE s.id = :id AND s.exam.id = :examId")
    Optional<ExamSection> findByIdAndExamIdWithQuestions(UUID id, UUID examId);
}
