package com.grassland.tagging.web.dto;

import com.grassland.tagging.repository.entity.SubtagEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionDTO {
    private int questionId;
    private String question;
    private List<String> tags;
    private List<SubtagEntity> subtags;
    private String createdAt;
    //formatted Time, 타임존에 맞게 시간 출력되도록 DTO만 String으로 변환
}