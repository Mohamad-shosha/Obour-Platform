package com.platform.obour.entity;

import com.platform.obour.entity.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_templates",
       indexes = { @Index(name = "idx_tmpl_domain", columnList = "domain_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentTemplate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "name_ar", length = 200)
    private String nameAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private AssessmentDomain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AssessmentCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private AssessmentTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private DifficultyLevel difficulty = DifficultyLevel.MIXED;

    @Column(name = "question_count")
    private Integer questionCount = 20;

    @Column(name = "time_limit_mins")
    private Integer timeLimitMins = 30;

    @Column(name = "passing_score")
    private Integer passingScore = 60;

    @Column(name = "allow_resume")
    private Boolean allowResume = true;

    private Boolean randomize = true;

    @Column(name = "show_explanation")
    private Boolean showExplanation = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
