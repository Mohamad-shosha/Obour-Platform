package com.platform.obour.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentCategoryDTO {
    private Long id;
    private Long domainId;
    private String name;
    private String nameAr;
    private String slug;
    private String icon;
    private String description;
    private Integer orderIndex;
    private Long questionCount;
    private List<AssessmentTopicDTO> topics;
}
