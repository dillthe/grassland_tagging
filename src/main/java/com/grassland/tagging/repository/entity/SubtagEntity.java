package com.grassland.tagging.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Subtag")
public class SubtagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtag_id")
    private int subtagId;

    @Column(name = "subtag_name")
    private String subtagName;

    @JsonManagedReference
    @ManyToMany(mappedBy = "subtagEntities", fetch = FetchType.LAZY)
    private List<TagEntity> tags = new ArrayList<>();
}