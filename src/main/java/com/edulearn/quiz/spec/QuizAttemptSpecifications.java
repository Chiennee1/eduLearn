package com.edulearn.quiz.spec;

import com.edulearn.quiz.entity.QuizAttempt;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public final class QuizAttemptSpecifications {

    private QuizAttemptSpecifications() {
    }

    public static Specification<QuizAttempt> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<QuizAttempt> hasQuizId(Long quizId) {
        return (root, query, cb) -> quizId == null
                ? cb.conjunction()
                : cb.equal(root.get("quiz").get("id"), quizId);
    }

    public static Specification<QuizAttempt> hasPassed(Boolean passed) {
        return (root, query, cb) -> passed == null
                ? cb.conjunction()
                : cb.equal(root.get("passed"), passed);
    }

    public static Specification<QuizAttempt> startedAtFrom(LocalDateTime startedFrom) {
        return (root, query, cb) -> startedFrom == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("startedAt"), startedFrom);
    }

    public static Specification<QuizAttempt> startedAtTo(LocalDateTime startedTo) {
        return (root, query, cb) -> startedTo == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("startedAt"), startedTo);
    }
}

