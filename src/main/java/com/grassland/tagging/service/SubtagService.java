package com.grassland.tagging.service;

import com.grassland.tagging.repository.SubtagRepository;
import com.grassland.tagging.repository.TagRepository;
import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.service.exceptions.ConflictException;
import com.grassland.tagging.service.exceptions.NotAcceptException;
import com.grassland.tagging.service.exceptions.NotFoundException;
import com.grassland.tagging.service.mapper.SubtagMapper;
import com.grassland.tagging.web.dto.SubtagBody;
import com.grassland.tagging.web.dto.SubtagDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubtagService {
    private final SubtagRepository subtagRepository;
    private final TagRepository tagRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubtagService.class);


    // 서브태그 추가
    public SubtagDTO createSubtag(int tagId, SubtagBody subtagBody) {
        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + tagId + " not found"));

        SubtagEntity subtagEntity;
        Optional<SubtagEntity> existingSubtag = subtagRepository.findBySubtagName(subtagBody.getSubtagName());
        if (existingSubtag.isPresent()) {
            subtagEntity = existingSubtag.get();  // 기존 엔티티 재사용
            log.info("Existing subtagEntity found: {},{}", subtagEntity.getSubtagId(), subtagEntity.getSubtagName());
        } else {
            subtagEntity = SubtagMapper.INSTANCE.idAndSubtagBodyToSubtagEntity(null, subtagBody);
            log.info("Created new subtagEntity: {},{}", subtagEntity.getSubtagId(), subtagEntity.getSubtagName());
            try {
                subtagEntity = subtagRepository.save(subtagEntity);
                log.info("Saved new subtagEntity: {},{}", subtagEntity.getSubtagId(), subtagEntity.getSubtagName());
            } catch (DataIntegrityViolationException e) {
                log.error("중복된 Subtag 저장 시도: subtagName='{}'이 이미 존재합니다.", subtagEntity.getSubtagName(), e);
                throw new ConflictException("이미 존재하는 subtagName입니다: " + subtagEntity.getSubtagName());
            }
        }

        // 기존 SubtagEntity라도 새로운 TagEntity와 연결될 수 있도록 설정
        if (!subtagEntity.getTags().contains(tagEntity)) {
            subtagEntity.getTags().add(tagEntity);
            tagEntity.getSubtagEntities().add(subtagEntity);
            tagRepository.save(tagEntity); // TagEntity 저장
            log.info("Linked subtagEntity to new TagEntity: {} -> {}", subtagEntity.getSubtagName(), tagEntity.getTag());
            //기존에 등록된 Subtag가 새로운 상위Tag의 Subtag로 등록!
            // (다대다, 하나의 상위태그는 여러개의 하위태그를, 하나의 하위태그는 여러개의 하위태그를 가질 수 있음)

        } else {
            log.info("SubtagEntity is already linked to this TagEntity.");
        }

        return SubtagMapper.INSTANCE.subtagEntityToSubtagDTO(subtagEntity);
    }

}
//    Set<SubtagEntity> subtagEntities = new HashSet<>();
//// TagEntity와 SubtagEntity 관계 설정
//        subtagEntity.getTags().add(tagEntity);
//                subtagEntity.add(subtagEntity);
//                tagEntity.getSubtagEntities().add(subtagEntity);
//
//                // 새로운 서브태그인 경우에만 저장 (이미 존재하는 경우는 업데이트만 진행)
//                if (!existingSubtag.isPresent()) {
//                try {
//                subtagEntity = subtagRepository.save(subtagEntity);
//                } catch (DataIntegrityViolationException e) {
//                throw new ConflictException("이미 상위 태그에 존재하는 subtagName입니다: " + subtagEntity.getSubtagName());
//                }
//               } else {
//                log.info("SubtagEntity already exists, skipping save.");
//                }
//
//                return SubtagMapper.INSTANCE.subtagEntityToSubtagDTO(subtagEntity);
//                }


    //서브태그 이름으로 상위태그 조회?
    public String findSubtag(SubtagBody subtagBody) {
        Optional<SubtagEntity> existingSubtags = subtagRepository.findBySubtagName(subtagBody.getSubtagName());

//        List<SubtagEntity> existingSubtags =subtagRepository.findBySubtagName(subtagBody.getSubtagName());
        if (existingSubtags.isEmpty()) {
            throw new NotFoundException("No subtag found matching: " + subtagBody.getSubtagName());
        }
        return existingSubtags.stream()
                .map(subtag -> "Subtag: " + subtag.getSubtagName() +
                        ", Tag: " + subtag.getTags())
//                        +
//                        ", Tag Id: " + subtag.getTags().stream().filter(TagEntity::getTagId))
                .collect(Collectors.joining("\n"));
    }
    
    // 서브태그 전체 조회
    public List<SubtagDTO> getAllSubtags() {
        List<SubtagEntity> subtagEntities = subtagRepository.findAll();
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return subtagDTOs;
    }


    //카테고리별 서브태그 조회
    public List<SubtagDTO> getSubtagsByTagId(int TagId) {
        TagEntity TagEntity = tagRepository.findById(TagId)
                .orElseThrow(()->new NotFoundException("Tag with ID " + TagId + " not found"));

        List<SubtagEntity> subtagEntities = subtagRepository.findAllByTags(TagEntity);
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return subtagDTOs;
    }



    // 서브태그 여러개 추가
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



