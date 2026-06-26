package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "session_questions",
       indexes = { @Index(name = "idx_sq_session", columnList = "session_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionQuestion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AssessmentSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "is_flagged")
    private Boolean isFlagged = false;
}
