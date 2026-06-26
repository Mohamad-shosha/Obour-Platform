package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "domain_scores",
       indexes = { @Index(name = "idx_ds_result", columnList = "result_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DomainScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private AssessmentResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    private AssessmentDomain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AssessmentCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private AssessmentTopic topic;

    private Integer questions = 0;
    private Integer correct = 0;

    @Column(name = "score_pct", precision = 5, scale = 2)
    private BigDecimal scorePct = BigDecimal.ZERO;
}
