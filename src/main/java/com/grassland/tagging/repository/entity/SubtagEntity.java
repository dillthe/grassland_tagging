package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Subtag")
public class SubtagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtag_id")
    private int subtagId;

    @Column(name = "subtag_name")
    private String subtagName;

    @ManyToMany
    @JoinTable(
            name = "tag_subtag", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "subtag_id"), // 서브태그 외래키
            inverseJoinColumns = @JoinColumn(name = "tag_id") // 태그 외래키
    )
    private List<TagEntity> tags = new ArrayList<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "subtags")
    private List<QuestionEntity> questions = new ArrayList<>();

    @Override
    public String toString() {
        return "SubtagEntity{" +
                "subtagId=" + subtagId +
                ", subtagName='" + subtagName + '\'' +
                '}';
    }


}