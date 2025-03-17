package com.grassland.tagging.service.mapper;

import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.web.dto.SubtagBody;
import com.grassland.tagging.web.dto.SubtagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubtagMapper {
    SubtagMapper INSTANCE = Mappers.getMapper(SubtagMapper.class);
    @Mapping(target = "subtagName", source = "subtagBody.subtagName")
    SubtagEntity idAndSubtagBodyToSubtagEntity(Integer subtagId, SubtagBody subtagBody);

    SubtagDTO subtagEntityToSubtagDTO(SubtagEntity subtagEntity);
    List<SubtagDTO> subtagEntitiesToSubtagDTOs(List<SubtagEntity> subtagEntities);


}