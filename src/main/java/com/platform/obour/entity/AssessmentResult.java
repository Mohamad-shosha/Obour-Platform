package com.platform.obour.entity;

import com.platform.obour.entity.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessment_results",
       indexes = {
           @Index(name = "idx_result_user",    columnList = "user_id"),
           @Index(name = "idx_result_session", columnList = "session_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private AssessmentSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private AssessmentTemplate template;

    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    private Integer answered = 0;
    private Integer correct = 0;

    @Column(name = "score_percent", precision = 5, scale = 2)
    private BigDecimal scorePercent = BigDecimal.ZERO;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "earned_points")
    private Integer earnedPoints = 0;

    private Boolean passed = false;

    @Column(name = "time_taken_secs")
    private Integer timeTakenSecs = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_achieved", length = 20)
    private DifficultyLevel levelAchieved = DifficultyLevel.BEGINNER;

    @Column(name = "completed_at")
    private LocalDateTime completedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DomainScore> domainScores;
}
