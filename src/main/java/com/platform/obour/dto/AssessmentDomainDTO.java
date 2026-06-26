package com.platform.obour.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentDomainDTO {
    private Long id;
    private String name;
    private String nameAr;
    private String slug;
    private String icon;
    private String color;
    private String gradient;
    private String description;
    private String descriptionAr;
    private Integer orderIndex;
    private Boolean isActive;
    private Long questionCount;
    private List<AssessmentCategoryDTO> categories;
}
