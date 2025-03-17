package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // 양방향 관계에서 반대편을 지정 (하위태그를 가진 필드)
    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "tag_subtag",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "tag_id"), // 상위태그(주체)의 외래키
            inverseJoinColumns = @JoinColumn(name = "subtag_id")  // 하위태그의 외래키
    )
    private List<SubtagEntity> subtagEntities = new ArrayList<>();


    @JsonBackReference  // 순환 참조 방지
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")  // mappedBy로 관계 주도
    private Set<QuestionEntity> questions = new HashSet<>();

}