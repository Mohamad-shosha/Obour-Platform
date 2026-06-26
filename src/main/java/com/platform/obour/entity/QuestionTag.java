package com.platform.obour.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_tags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;
}
