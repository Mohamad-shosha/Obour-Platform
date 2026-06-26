package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessment_domains",
       indexes = {
           @Index(name = "idx_domain_slug",   columnList = "slug"),
           @Index(name = "idx_domain_active",  columnList = "is_active")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentDomain {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 100)
    private String icon;

    @Column(length = 20)
    private String color;

    @Column(length = 200)
    private String gradient;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<AssessmentCategory> categories;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionBank> questions;
}
