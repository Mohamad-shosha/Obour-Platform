package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionReviewDTO {
    private Long questionId;
    private String questionText;
    private String questionTextAr;
    private Long userChoiceId;
    private String userChoiceText;
    private String userChoiceTextAr;
    private Boolean isCorrect;
    private Long correctChoiceId;
    private String explanation;
}
