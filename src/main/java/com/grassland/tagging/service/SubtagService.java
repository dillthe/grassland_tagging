package com.grassland.tagging.service;

import com.grassland.tagging.repository.SubtagRepository;
import com.grassland.tagging.repository.TagRepository;
import com.grassland.tagging.repository.entity.QuestionEntity;
import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.service.exceptions.NotAcceptException;
import com.grassland.tagging.service.exceptions.NotFoundException;
import com.grassland.tagging.service.mapper.QuestionMapper;
import com.grassland.tagging.service.mapper.SubtagMapper;
import com.grassland.tagging.service.mapper.TagMapper;
import com.grassland.tagging.web.dto.QuestionDTO;
import com.grassland.tagging.web.dto.SubtagBody;
import com.grassland.tagging.web.dto.SubtagDTO;
import com.grassland.tagging.web.dto.TagBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubtagService {
    private final SubtagRepository subtagRepository;
    private final TagRepository tagRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubtagService.class);


    // 키워드 전체 조회
    public List<SubtagDTO> getAllSubtags() {
        List<SubtagEntity> subtagEntities = subtagRepository.findAll();
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return subtagDTOs;
    }


    //카테고리별 키워드 조회
    public List<SubtagDTO> getSubtagsByTagId(int TagId) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(()->new NotFoundException("Tag with ID " + TagId + " not found"));

        List<SubtagEntity> subtagEntities = subtagRepository.findAllByTags(TagEntity);
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return subtagDTOs;
    }

    // 키워드 추가
    public SubtagDTO createSubtag(int tagId, SubtagBody subtagBody) {
//            boolean subtagExists = tagRepository.existsByTag(subtagBody.getSubtagName());
//            if (subtagExists) {
//                throw new NotAcceptException("Subtag with the same name: " + subtagBody.getSubtagName() + " already exists.");
//            }
//
//            TagEntity tagEntity = TagMapper.INSTANCE.idAndTagBodyToTagEntity(null, tagBody);
//            log.info(tagEntity.toString());
//            TagEntity tagCreated = tagRepository.save(tagEntity);
//            return "ID:"+ tagCreated.getTagId() + " "+tagCreated.getTag();
//        }
//

        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + tagId + " not found"));
        log.info("Found tagEntity: {}{}", tagEntity.getTag(), tagEntity.getTagId());

        SubtagEntity subtagEntity = SubtagMapper.INSTANCE.idAndSubtagBodyToSubtagEntity(null, subtagBody);
        log.info("Created subtagEntity: {},{}", subtagEntity.getSubtagId(),subtagEntity.getSubtagName());

        subtagEntity.getTags().add(tagEntity);
        log.info("SubtagEntity with tags: {}", subtagEntity.getTags().toString());  // SubtagEntity에 추가된 tags 확인

        tagEntity.getSubtagEntities().add(subtagEntity);
        log.info("TagEntity with subtags: {}", tagEntity.getSubtagEntities().toString());  // TagEntity에 추가된 subtags 확인

        SubtagEntity savedSubtag = subtagRepository.save(subtagEntity);
        log.info("Saved subtagEntity: {},{}", savedSubtag.getSubtagId(), savedSubtag.getSubtagName());  // 저장된 SubtagEntity 확인

        SubtagDTO subtagDTO = SubtagMapper.INSTANCE.subtagEntityToSubtagDTO(savedSubtag);
        return subtagDTO;
    }
//
//        TagEntity tagEntity = tagRepository.findById(tagId)
//                .orElseThrow(() -> new NotFoundException("Tag with ID " + tagId + " not found"));
//
//        // 빈 리스트가 아니라면, 초기화가 제대로 되어있을 것입니다.
//        if (tagEntity.getSubtagEntities() == null) {
//            tagEntity.setSubtagEntities(new ArrayList<>());
//        }
//
//        for (SubtagEntity subtag : tagEntity.getSubtagEntities()) {
//            if (subtag.getSubtagName().equals(subtagBody.getSubtagName())) {
//                throw new NotAcceptException("Subtag: " + subtagBody.getSubtagName() + " is already added.");
//            }
//        }
//
//        SubtagEntity subtagEntity = SubtagMapper.INSTANCE.idAndSubtagBodyToSubtagEntity(null, subtagBody);
//
//        // 3. Tag와 SubTag 연결 (다대다 관계)
//        tagEntity.getSubtagEntities().add(subtagEntity); // TagEntity에 SubTagEntity 추가
//        subtagEntity.getTags().add(tagEntity);
//        SubtagEntity savedSubtag = subtagRepository.save(subtagEntity);
//        SubtagDTO subtagDTO = SubtagMapper.INSTANCE.subtagEntityToSubtagDTO(savedSubtag);
//        return subtagDTO;
//    }



    // 키워드 여러개 추가
    public List<SubtagDTO> createSubtags(int TagId, List<SubtagBody> subtagBodies) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " not found"));

        List<SubtagEntity> savedSubtagList = new ArrayList<>();

        for (SubtagBody subtagBody : subtagBodies) {
            boolean subtagExists = subtagRepository.existsByTagsAndSubtagName(TagEntity, subtagBody.getSubtagName());
            if (subtagExists) {
                throw new NotAcceptException("Subtag: " + subtagBody.getSubtagName() + " is already added.");
            }
            SubtagEntity subtagEntity = SubtagMapper.INSTANCE.idAndSubtagBodyToSubtagEntity(null, subtagBody);
//            subtagEntity.setTagEntity(TagEntity);
            SubtagEntity savedSubtag = subtagRepository.save(subtagEntity);
            savedSubtagList.add(savedSubtag);

        }List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(savedSubtagList);

        return subtagDTOs;
    }

    //키워드 이름으로 카테고리 조회
    public String findSubtag(SubtagBody subtagBody) {
        List<SubtagEntity> existingSubtags =subtagRepository.findBySubtagName(subtagBody.getSubtagName());
        if (existingSubtags.isEmpty()) {
            throw new NotFoundException("No subtags found matching: " + subtagBody.getSubtagName());
        }
        return existingSubtags.stream()
                .map(subtag -> "Subtag: " + subtag.getSubtagName() +
                        ", Tag: " + subtag.getTags())
//                        +
//                        ", Tag Id: " + subtag.getTags().stream().filter(TagEntity::getTagId))
                .collect(Collectors.joining("\n"));
    }

    // 하위태그 삭제 - Subtag로 조회)
    public String deleteSubtag(int TagId, SubtagBody subtagBody) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));

        SubtagEntity existingSubtag =subtagRepository.findBySubtagNameAndTags(subtagBody.getSubtagName(), TagEntity)
                .orElseThrow(() -> new NotFoundException("Subtag doesn't exist in the specified Tag"));
            subtagRepository.delete(existingSubtag);
            return "Subtag Id: " + existingSubtag.getSubtagId() + ", Subtag Name: " + existingSubtag.getSubtagName() + " is deleted.";
    }

    // 하위태그 삭제(하위태그 및 상위태그 Id로 조회)
    public String deleteSubtagByTagId(int TagId, int subtagId) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));

        SubtagEntity existingSubtag = subtagRepository.findBySubtagIdAndTags(subtagId, TagEntity)
                .orElseThrow(() -> new NotFoundException("Subtag doesn't exist in the specified Tag"));
        subtagRepository.deleteById(existingSubtag.getSubtagId());
        return String.format("Subtag Id: %d, Subtag Name: %s is deleted.", existingSubtag.getSubtagId(), existingSubtag.getSubtagName());
    }

    //하위태그 삭제 (해당 상위태그 내 전체 하위태그 삭제)
    public String deleteAllSubtagsByTagId(int TagId) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));

        List<SubtagEntity> existingSubtags = subtagRepository.findAllByTags(TagEntity);
        if (existingSubtags.isEmpty()) {
            throw new NotFoundException("No subtags found in the specified Tag");
        }
        subtagRepository.deleteAll(existingSubtags);
        return "Deleted All" + existingSubtags.size() + " Subtags in Tag: " + TagEntity.getTag();
    }
}



