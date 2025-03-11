package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Question")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private int questionId;

    @Lob
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    // UTC 기준으로 저장
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    @JsonManagedReference
    @ManyToMany(mappedBy = "questions")
    private Set<TagEntity> tags = new HashSet<>();
}