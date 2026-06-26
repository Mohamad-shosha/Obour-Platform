package com.platform.obour.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionBankDTO {
    private Long id;
    private Long domainId;
    private String domainName;
    private Long categoryId;
    private String categoryName;
    private Long topicId;
    private String topicName;
    private String questionText;
    private String questionTextAr;
    private String questionType;
    private String difficulty;
    private String explanation;
    private String codeSnippet;
    private String codeLanguage;
    private Integer points;
    private Integer timeLimitSecs;
    private List<QuestionBankChoiceDTO> choices;
    private List<String> tags;
}
