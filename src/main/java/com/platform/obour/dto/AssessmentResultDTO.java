package com.platform.obour.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentResultDTO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Long templateId;
    private String templateName;
    private String domainName;
    private String domainNameAr;
    private String domainColor;
    private Integer totalQuestions;
    private Integer answered;
    private Integer correct;
    private BigDecimal scorePercent;
    private Integer totalPoints;
    private Integer earnedPoints;
    private Boolean passed;
    private Integer timeTakenSecs;
    private String levelAchieved;
    private LocalDateTime completedAt;
    private List<DomainScoreDTO> domainScores;
    private List<QuestionReviewDTO> questionReviews;
}
