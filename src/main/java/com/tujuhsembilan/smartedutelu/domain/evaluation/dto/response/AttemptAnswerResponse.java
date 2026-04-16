package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttemptAnswer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AttemptAnswerResponse {

    private UUID id;
    private UUID questionId;
    private String answer;
    private BigDecimal score;
    private Boolean isCorrect;
    private Integer timeSpentSeconds;
    private OffsetDateTime answeredAt;

    public static AttemptAnswerResponse from(ExamAttemptAnswer a) {
        return AttemptAnswerResponse.builder()
                .id(a.getId())
                .questionId(a.getQuestion().getId())
                .answer(a.getAnswer())
                .score(a.getScore())
                .isCorrect(a.getIsCorrect())
                .timeSpentSeconds(a.getTimeSpentSeconds())
                .answeredAt(a.getAnsweredAt())
                .build();
    }
}
