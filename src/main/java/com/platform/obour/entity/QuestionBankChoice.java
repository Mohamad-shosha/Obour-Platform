package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_bank_choices",
       indexes = { @Index(name = "idx_qbc_question", columnList = "question_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionBankChoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(name = "choice_text", columnDefinition = "TEXT", nullable = false)
    private String choiceText;

    @Column(name = "choice_text_ar", columnDefinition = "TEXT")
    private String choiceTextAr;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "order_index")
    private Integer orderIndex = 0;
}
