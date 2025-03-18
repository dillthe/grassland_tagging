package com.grassland.tagging.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagDTO {
    private int tagId;
    private String tag;
    private int questionCount;
    private List<SubtagDTO> subtagEntities;
}
