//package com.grassland.tagging.service.mapper;
//
//import com.grassland.tagging.repository.entity.SubTagEntity;
//import com.grassland.tagging.web.dto.SubTagBody;
//import com.grassland.tagging.web.dto.SubTagDTO;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
//@Mapper
//public interface SubTagMapper {
//    SubTagMapper INSTANCE = Mappers.getMapper(SubTagMapper.class);
//    @Mapping(target = "subTag", source = "subTagBody.subTag")
//    SubTagEntity idAndSubTagBodyToSubTagEntity(Integer subTagId, SubTagBody subTagBody);
//
//    @Mapping(source="tagEntity.tagId", target="tagId")
//    @Mapping(source="tagEntity.name", target="tagName")
//    SubTagDTO subTagEntityToSubTagDTO(SubTagEntity subTagEntity);
//    @Mapping(source="tagEntity.tagId", target="tagId")
//    @Mapping(source="tagEntity.name", target="tagName")
//    List<SubTagDTO> subTagEntitiesToSubTagDTOs(List<SubTagEntity> subTagEntities);
//
//
//}