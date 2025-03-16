//package com.grassland.tagging.repository;
//
//import com.grassland.tagging.repository.entity.TagEntity;
//import com.grassland.tagging.repository.entity.SubTagEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SubTagRepository extends JpaRepository<SubTagEntity, Integer> {
//
//    List<SubTagEntity> findAllByTagEntity(TagEntity tagEntity);
//
//
//    boolean existsByTagEntityAndSubTag(TagEntity tagEntity, String subTag);
//
//
//    Optional<SubTagEntity> findBySubTagAndTagEntity(String subTag, TagEntity tagEntity);
//
//    Optional<SubTagEntity> findBySubTagIdAndTagEntity(int subTagId, TagEntity tagEntity);
//
//
//    List<SubTagEntity> findBySubTag(String firstMatchedSubTag);
//}
//
