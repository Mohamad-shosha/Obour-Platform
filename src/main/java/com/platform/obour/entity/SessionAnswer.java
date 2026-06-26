package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_answers",
       indexes = { @Index(name = "idx_sa_session", columnList = "session_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionAnswer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AssessmentSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id")
    private QuestionBankChoice choice;

    @Column(name = "text_answer", columnDefinition = "TEXT")
    private String textAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "points_earned")
    private Integer pointsEarned = 0;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt = LocalDateTime.now();
}
