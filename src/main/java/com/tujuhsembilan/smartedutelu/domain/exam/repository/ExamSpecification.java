package com.tujuhsembilan.smartedutelu.domain.exam.repository;

import com.tujuhsembilan.smartedutelu.domain.exam.entity.Exam;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ExamSpecification {

    private ExamSpecification() {}

    public static Specification<Exam> hasTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    public static Specification<Exam> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Exam> hasExamType(String examType) {
        return (root, query, cb) -> cb.equal(root.get("examType"), examType);
    }

    public static Specification<Exam> hasCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Exam> searchKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
