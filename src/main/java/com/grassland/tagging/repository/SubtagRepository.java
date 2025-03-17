package com.grassland.tagging.repository;

import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.repository.entity.SubtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtagRepository extends JpaRepository<SubtagEntity, Integer> {


//    List<TagEntity> findAllBySubtagEntities(SubtagEntity subtagEntity);
    // 특정 TagEntity에 속하는 모든 SubtagEntity 조회
    List<SubtagEntity> findAllByTags(TagEntity tag);

    // 특정 TagEntity와 subtag를 가진 SubtagEntity 존재 여부 확인
    boolean existsByTagsAndSubtagName(TagEntity tag, String subtag);

    // 특정 TagEntity와 subtag를 가진 SubtagEntity 찾기
    Optional<SubtagEntity> findBySubtagNameAndTags(String subtag, TagEntity tag);

    // 특정 TagEntity와 subtagId를 가진 SubtagEntity 찾기
    Optional<SubtagEntity> findBySubtagIdAndTags(int subtagId, TagEntity tag);

    // subtag 값이 특정 문자열과 일치하는 모든 SubtagEntity 조회
//    List<SubtagEntity> findBySubtagName(String firstMatchedSubtag);

    Optional<SubtagEntity> findBySubtagName(String subtagName);
}
//
//    List<SubtagEntity> findAllByTagEntity(TagEntity tagEntity);
//
//
//    boolean existsByTagEntityAndSubtag(TagEntity tagEntity, String subtag);
//
//
//    Optional<SubtagEntity> findBySubtagAndTagEntity(String subtag, TagEntity tagEntity);
//
//    Optional<SubtagEntity> findBySubtagIdAndTagEntity(int subtagId, TagEntity tagEntity);
//
//
//    List<SubtagEntity> findBySubtag(String firstMatchedSubtag);
//}

