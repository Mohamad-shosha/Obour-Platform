package com.platform.obour.entity;

import com.platform.obour.entity.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessment_sessions",
       indexes = {
           @Index(name = "idx_sess_user",   columnList = "user_id"),
           @Index(name = "idx_sess_status", columnList = "status"),
           @Index(name = "idx_sess_tmpl",   columnList = "template_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private AssessmentTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SessionStatus status = SessionStatus.STARTED;

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "time_spent_secs")
    private Integer timeSpentSecs = 0;

    @Column(name = "current_question_index")
    private Integer currentQuestionIndex = 0;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<SessionQuestion> sessionQuestions;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SessionAnswer> answers;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AssessmentResult result;
}
