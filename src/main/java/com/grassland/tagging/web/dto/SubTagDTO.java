package com.grassland.tagging.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubTagDTO {
    private int subTagId;
    private int tagId;
    //하위태그 조회 시 상위태그 명도 불러오고 싶은 경우 사용 - 하위태그 중복 있는지 확인할 때 사용하기 좋음(findsubTags 메소드)
    private String tagName;
    private String subTag;
}