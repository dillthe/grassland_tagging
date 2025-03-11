package com.grassland.tagging.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagDTO {
    private int tagId;
    private String tag;
    private int questionCount;
}
