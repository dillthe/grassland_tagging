package com.grassland.tagging.repository;

import com.grassland.tagging.repository.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    Optional<TagEntity> findByTag(String tagName);

    boolean existsByTag(String tag);
}
