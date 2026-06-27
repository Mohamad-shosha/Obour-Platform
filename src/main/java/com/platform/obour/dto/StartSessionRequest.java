package com.platform.obour.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StartSessionRequest {
    private Long userId;
    private Long templateId;
    private Long categoryId;
    // Optional: resume existing session instead of creating new one
    private Boolean resumeIfExists = true;
}
