package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessment_topics",
       indexes = {
           @Index(name = "idx_topic_category", columnList = "category_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentTopic {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AssessmentCategory category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "name_ar", length = 150)
    private String nameAr;

    @Column(nullable = false, length = 150)
    private String slug;

    @Column(length = 100)
    private String icon;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<QuestionBank> questions;
}
