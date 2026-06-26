package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SaveAnswerRequest {
    private Long questionId;
    private Long choiceId;
    private String textAnswer;
    private Boolean isFlagged;
    private Integer timeSpentSecs;
}
