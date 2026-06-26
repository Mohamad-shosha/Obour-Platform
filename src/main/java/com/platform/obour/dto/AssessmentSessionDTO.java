package com.platform.obour.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentSessionDTO {
    private Long id;
    private Long userId;
    private Long templateId;
    private String templateName;
    private String templateNameAr;
    private Integer questionCount;
    private Integer timeLimitMins;
    private Integer passingScore;
    private Boolean showExplanation;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Integer timeSpentSecs;
    private Integer currentQuestionIndex;
    private Integer answeredCount;
    private List<QuestionBankDTO> questions;
    private List<SaveAnswerRequest> savedAnswers;
}
