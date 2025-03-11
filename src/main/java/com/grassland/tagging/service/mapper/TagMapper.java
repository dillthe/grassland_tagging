package com.grassland.tagging.service.mapper;

import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.web.dto.TagBody;
import com.grassland.tagging.web.dto.TagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    @Mapping(target = "questionCount", expression = "java(tag.getQuestions().size())")
    TagDTO tagEntityToTagDTO(TagEntity tag);

    List<TagDTO> tagEntitiesToTagDTOs(List<TagEntity> tagEntities);

    default String tagEntityToString(TagEntity tagEntity) {
        return tagEntity != null ? tagEntity.getTag() : null;
    }

    TagEntity idAndTagBodyToTagEntity(Integer id, TagBody tagBody);
}