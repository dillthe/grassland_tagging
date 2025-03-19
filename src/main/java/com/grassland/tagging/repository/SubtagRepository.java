package com.grassland.tagging.repository;

import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtagRepository extends JpaRepository<SubtagEntity, Integer> {

    // 특정 TagEntity에 속하는 모든 SubtagEntity 조회
    List<SubtagEntity> findAllByTags(TagEntity tag);

    // 특정 TagEntity와 subtagId를 가진 SubtagEntity 찾기
    Optional<SubtagEntity> findBySubtagIdAndTags(int subtagId, TagEntity tag);

    Optional<SubtagEntity> findBySubtagName(String subtagName);
}