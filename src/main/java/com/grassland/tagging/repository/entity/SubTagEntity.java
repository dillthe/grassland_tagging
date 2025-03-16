package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Subtag")
public class SubTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtag_id")
    private int subTagId;

    @Column(name = "subtag")
    private String subTag;

    @JsonManagedReference
    @ManyToMany(mappedBy = "subTagEntities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)  // 상위태그에서 정의한 필드명 사용
    private List<TagEntity> tags;
}