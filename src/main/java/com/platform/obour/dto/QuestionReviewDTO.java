package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionReviewDTO {
    private Long questionId;
    private String questionText;
    private String selectedChoiceText;
    private String correctChoiceText;
    private Boolean isCorrect;
    private String explanation;
    private String difficulty;
    private Integer pointsEarned;
    private Integer maxPoints;
}
