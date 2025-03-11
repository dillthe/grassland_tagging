package com.grassland.tagging.service.mapper;

import com.grassland.tagging.repository.entity.QuestionEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.web.dto.QuestionBody;
import com.grassland.tagging.web.dto.QuestionDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
@Mapper(uses = TagMapper.class)
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "question", source = "questionBody.question")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    QuestionEntity idAndQuestionBodyToQuestionEntity(Integer id, QuestionBody questionBody);

    @Mapping(target="questionId", source = "questionId")
    @Mapping(target="tags", source="tags")
    @Mapping(target = "createdAt", source = "createdAt") // createdAt을 Instant로 그대로 매핑
    QuestionDTO questionEntityToQuestionDTO(QuestionEntity questionEntity);

    @Mapping(target="questionId", source = "questionId")
    @Mapping(target="categoryId", source="categoryId")
    List<QuestionDTO> questionEntitiesToQuestionDTOs(List<QuestionEntity> questionEntities);

    // Set<TagEntity>를 Set<String>으로 변환 (tag 이름만 추출)
    @IterableMapping(elementTargetType = String.class)
    Set<String> tagEntitiesToStrings(Set<TagEntity> tags);
}