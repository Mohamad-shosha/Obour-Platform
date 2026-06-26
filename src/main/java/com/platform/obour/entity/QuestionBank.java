package com.platform.obour.entity;

import com.platform.obour.entity.enums.DifficultyLevel;
import com.platform.obour.entity.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question_bank",
       indexes = {
           @Index(name = "idx_qb_domain",     columnList = "domain_id"),
           @Index(name = "idx_qb_category",   columnList = "category_id"),
           @Index(name = "idx_qb_topic",      columnList = "topic_id"),
           @Index(name = "idx_qb_difficulty", columnList = "difficulty"),
           @Index(name = "idx_qb_type",       columnList = "question_type"),
           @Index(name = "idx_qb_active",     columnList = "is_active")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionBank {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    private AssessmentDomain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AssessmentCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private AssessmentTopic topic;

    // backward compat with old sections table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "question_text_ar", columnDefinition = "TEXT")
    private String questionTextAr;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 30)
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private DifficultyLevel difficulty = DifficultyLevel.BEGINNER;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "explanation_ar", columnDefinition = "TEXT")
    private String explanationAr;

    @Column(name = "code_snippet", columnDefinition = "MEDIUMTEXT")
    private String codeSnippet;

    @Column(name = "code_language", length = 50)
    private String codeLanguage;

    private Integer points = 1;

    @Column(name = "time_limit_secs")
    private Integer timeLimitSecs = 60;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<QuestionBankChoice> choices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "question_bank_tags",
               joinColumns = @JoinColumn(name = "question_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<QuestionTag> tags;
}
