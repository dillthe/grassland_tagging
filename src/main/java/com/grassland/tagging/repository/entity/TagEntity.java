package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int tagId;

    @Size(max = 255, message = "태그 길이는 255자를 넘을 수 없습니다.")
    @Column(name = "tag_name", nullable = false)
    private String tag;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<SubtagEntity> subtagEntities = new HashSet<>();

    @JsonBackReference  // 순환 참조 방지
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")  // mappedBy로 관계 주도
    private Set<QuestionEntity> questions = new HashSet<>();
    @Override
    public String toString() {
        return "TagEntity{" +
                "tagId=" + tagId +
                ", tagName='" + tag + '\'' +
                '}';
    }
}