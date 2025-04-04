package com.grassland.tagging.service;

import com.grassland.tagging.repository.SubtagRepository;
import com.grassland.tagging.repository.TagRepository;
import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.service.exceptions.ConflictException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubtagService {
    private final SubtagRepository subtagRepository;
    private final TagRepository tagRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubtagService.class);


    // 서브태그 추가
    @Transactional
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
            //기존에 등록된 Subtag가 새로운 Tag의 Subtag로 등록!
            // (다대다, 하나의 상위태그는 여러개의 하위태그를, 하나의 하위태그는 여러개의 하위태그를 가질 수 있음)

        } else {
            log.info("SubtagEntity is already linked to this TagEntity.");
            throw new ConflictException("SubtagEntity is already linked to this TagEntity.");
        }

            return SubtagMapper.INSTANCE.subtagEntityToSubtagDTO(subtagEntity);
    }


    // 서브태그 여러개 추가
    @Transactional
    public List<SubtagDTO> createSubtags(int tagId, List<SubtagBody> subtagBodies) {
        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + tagId + " not found"));

        List<SubtagEntity> savedSubtagList = new ArrayList<>();
        SubtagEntity subtagEntity = null;

        for (SubtagBody subtagBody : subtagBodies) {
            Optional<SubtagEntity> existingSubtag = subtagRepository.findBySubtagName(subtagBody.getSubtagName());
            if (existingSubtag.isPresent()) {
                subtagEntity = existingSubtag.get();  // 기존 엔티티 재사용
                log.info("Existing subtagEntity found: {},{}", subtagEntity.getSubtagId(), subtagEntity.getSubtagName());
            } else {
                subtagEntity = SubtagMapper.INSTANCE.idAndSubtagBodyToSubtagEntity(null, subtagBody);
                try {
                    subtagEntity = subtagRepository.save(subtagEntity);
                    log.info("Saved new subtagEntity: {},{}", subtagEntity.getSubtagId(), subtagEntity.getSubtagName());
                } catch (DataIntegrityViolationException e) {
                    log.error("중복된 Subtag 저장 시도: subtagName='{}'이 이미 존재합니다.", subtagEntity.getSubtagName(), e);
                    throw new ConflictException("이미 존재하는 subtagName입니다: " + subtagEntity.getSubtagName());
                }
            }

            if (!subtagEntity.getTags().contains(tagEntity)) {
                subtagEntity.getTags().add(tagEntity);
                tagEntity.getSubtagEntities().add(subtagEntity);
                tagRepository.save(tagEntity); // TagEntity 저장
                log.info("Linked subtagEntity to new TagEntity: {} -> {}", subtagEntity.getSubtagName(), tagEntity.getTag());
                //기존에 등록된 Subtag가 새로운 Tag의 Subtag로 등록!
                // (다대다, 하나의 상위태그는 여러개의 하위태그를, 하나의 하위태그는 여러개의 하위태그를 가질 수 있음)

            } else {
                log.info("SubtagEntity is already linked to this TagEntity.");
                throw new ConflictException("SubtagEntity is already linked to this TagEntity.");
            }
        }
        savedSubtagList.add(subtagEntity);
        log.info(savedSubtagList.toString());
        return SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(savedSubtagList);
    }


    //서브태그 이름으로 상위태그 조회
    public String findSubtag(SubtagBody subtagBody) {
        List<SubtagEntity> existingSubtags = subtagRepository.findBySubtagName(subtagBody.getSubtagName())
                .map(List::of)  // Optional이 있으면 List로 변환
                .orElseGet(Collections::emptyList);  // 없으면 빈 리스트 반환


        return existingSubtags.stream()
                .map(subtag -> "SubtagId: " + subtag.getSubtagId() +
                        ", Subtag: " + subtag.getSubtagName() +
                        "\n, Tag: " + subtag.getTags())
               .collect(Collectors.joining("\n"));
    }
    
    // 서브태그 전체 조회
    public List<SubtagDTO> getAllSubtags() {
        List<SubtagEntity> subtagEntities = subtagRepository.findAll();
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return subtagDTOs;
    }


    //태그별 서브태그 조회
    public String getSubtagsByTagId(int tagId) {
        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(()->new NotFoundException("Tag with ID " + tagId + " not found"));

        List<SubtagEntity> subtagEntities = subtagRepository.findAllByTags(tagEntity);
        List<SubtagDTO> subtagDTOs = SubtagMapper.INSTANCE.subtagEntitiesToSubtagDTOs(subtagEntities);
        return "Tag Id: " + tagEntity.getTagId() + ", Tag: " + tagEntity.getTag() + ", Subtags:" + subtagDTOs;
    }




    // 하위태그 삭제 - Subtag name으로 조회
    public String deleteSubtag(SubtagBody subtagBody) {
        SubtagEntity existingSubtag =subtagRepository.findBySubtagName(subtagBody.getSubtagName())
                .orElseThrow(() -> new NotFoundException("Subtag doesn't exist in the specified Tag"));
        subtagRepository.delete(existingSubtag);
        return "Subtag Id: " + existingSubtag.getSubtagId() + ", Subtag Name: " + existingSubtag.getSubtagName() + " is deleted.";
    }


    // 하위태그 삭제(하위태그 및 상위태그 Id로 조회)
    public String deleteSubtagByTagId(int tagId, int subtagId) {
        TagEntity tagEntity = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + tagId + " doesn't exist"));

        SubtagEntity existingSubtag = subtagRepository.findBySubtagIdAndTags(subtagId, tagEntity)
                .orElseThrow(() -> new NotFoundException("Subtag doesn't exist in the specified Tag"));

        tagEntity.getSubtagEntities().remove(existingSubtag);
        tagRepository.save(tagEntity);

        existingSubtag.getTags().remove(tagEntity);
        subtagRepository.save(existingSubtag);

        return String.format("Subtag Id: %d, Subtag Name: %s is removed from TagId: %d.",
                existingSubtag.getSubtagId(), existingSubtag.getSubtagName(), tagId);
    }

    //하위태그 삭제 (해당 상위태그 내 전체 하위태그 삭제)
    public String deleteAllSubtags(){
        subtagRepository.deleteAll();
        return "Deleted All Subtags";
    }
}



