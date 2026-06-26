package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentTopicDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private String nameAr;
    private String slug;
    private String icon;
    private String description;
    private Integer orderIndex;
    private Long questionCount;
}
