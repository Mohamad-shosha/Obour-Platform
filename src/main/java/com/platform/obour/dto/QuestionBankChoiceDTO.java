package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionBankChoiceDTO {
    private Long id;
    private String choiceText;
    private String choiceTextAr;
    // NOTE: isCorrect is intentionally EXCLUDED from client responses during active assessment
    // It is only included in results/explanations responses
    private Boolean isCorrect;
    private String explanation;
    private Integer orderIndex;
}
