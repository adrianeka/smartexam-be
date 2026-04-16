package com.tujuhsembilan.smartedutelu.domain.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionResponse {

    private UUID id;
    private UUID examId;
    private String title;
    private String instruction;
    private Integer position;
    private Integer timeLimitSeconds;
    private Integer questionCount;
    private List<ExamQuestionResponse> questions;
}
