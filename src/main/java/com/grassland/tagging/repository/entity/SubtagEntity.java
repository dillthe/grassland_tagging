package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    private Set<TagEntity> tags = new HashSet<>();

    @Override
    public String toString() {
        return "SubtagEntity{" +
                "subtagId=" + subtagId +
                ", subtagName='" + subtagName + '\'' +
                '}';
    }
}