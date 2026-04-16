package com.tujuhsembilan.smartedutelu.domain.question.repository;

import com.tujuhsembilan.smartedutelu.domain.question.entity.Question;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class QuestionSpecification {

    private QuestionSpecification() {}

    public static Specification<Question> hasTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    public static Specification<Question> hasType(String type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Question> hasDifficulty(String difficulty) {
        return (root, query, cb) -> cb.equal(root.get("difficultyLevel"), difficulty);
    }

    public static Specification<Question> hasCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Question> hasFolder(UUID folderId) {
        return (root, query, cb) -> cb.equal(root.get("folder").get("id"), folderId);
    }

    public static Specification<Question> searchKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("questionText")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
