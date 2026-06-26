package com.platform.obour.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DomainScoreDTO {
    private Long domainId;
    private String domainName;
    private String domainNameAr;
    private String domainIcon;
    private String domainColor;
    private Long categoryId;
    private String categoryName;
    private Long topicId;
    private String topicName;
    private Integer questions;
    private Integer correct;
    private BigDecimal scorePct;
    private String strengthLevel; // STRONG (>=80%), MODERATE (60-79%), WEAK (<60%)
}
