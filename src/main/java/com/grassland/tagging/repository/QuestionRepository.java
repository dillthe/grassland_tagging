package com.grassland.tagging.repository;

import com.grassland.tagging.repository.entity.QuestionEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

}
