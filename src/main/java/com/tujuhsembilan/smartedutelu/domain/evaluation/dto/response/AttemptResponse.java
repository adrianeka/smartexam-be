package com.tujuhsembilan.smartedutelu.domain.evaluation.dto.response;

import com.tujuhsembilan.smartedutelu.domain.evaluation.entity.ExamAttempt;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AttemptResponse {

    private UUID id;
    private UUID examId;
    private UUID studentId;
    private UUID sessionId;
    private BigDecimal score;
    private Boolean passed;
    private Integer questionsAnswered;
    private Integer timeSpentSeconds;
    private String ipAddress;
    private String deviceInfo;
    private OffsetDateTime startedAt;
    private OffsetDateTime submittedAt;
    private List<AttemptAnswerResponse> answers;

    public static AttemptResponse from(ExamAttempt attempt) {
        return AttemptResponse.builder()
                .id(attempt.getId())
                .examId(attempt.getExam().getId())
                .studentId(attempt.getStudent().getId())
                .sessionId(attempt.getSession() != null ? attempt.getSession().getId() : null)
                .score(attempt.getScore())
                .passed(attempt.getPassed())
                .questionsAnswered(attempt.getQuestionsAnswered())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .ipAddress(attempt.getIpAddress())
                .deviceInfo(attempt.getDeviceInfo())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    public static AttemptResponse fromWithAnswers(ExamAttempt attempt) {
        var resp = from(attempt);
        resp.setAnswers(attempt.getAnswers() != null
                ? attempt.getAnswers().stream().map(AttemptAnswerResponse::from).toList()
                : List.of());
        return resp;
    }
}
