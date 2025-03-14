package com.grassland.tagging.repository;

import com.grassland.tagging.repository.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    Optional<TagEntity> findByTag(String tagName);

    boolean existsByTag(String tag);

    Set<TagEntity> findByTagIn(Collection<String> tags);
}
