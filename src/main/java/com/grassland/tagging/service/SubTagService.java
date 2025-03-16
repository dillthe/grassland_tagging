//package com.grassland.tagging.service;
//
//import com.grassland.tagging.repository.TagRepository;
//import com.grassland.tagging.repository.SubTagRepository;
//import com.grassland.tagging.repository.entity.TagEntity;
//import com.grassland.tagging.repository.entity.SubTagEntity;
//import com.grassland.tagging.repository.entity.QuestionEntity;
//import com.grassland.tagging.service.exceptions.NotAcceptException;
//import com.grassland.tagging.service.exceptions.NotFoundException;
//import com.grassland.tagging.service.mapper.SubTagMapper;
//import com.grassland.tagging.web.dto.SubTagBody;
//import com.grassland.tagging.web.dto.SubTagDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SubTagService {
//    private final SubTagRepository subTagRepository;
//    private final TagRepository tagRepository;
//    private static final Logger logger = LoggerFactory.getLogger(SubTagService.class);
//
//
//    // 키워드 전체 조회
//    public List<SubTagDTO> getAllSubTags() {
//        List<SubTagEntity> subTagEntities = subTagRepository.findAll();
//        List<SubTagDTO> subTagDTOs = SubTagMapper.INSTANCE.subTagEntitiesToSubTagDTOs(subTagEntities);
//        return subTagDTOs;
//    }
//
////    //카테고리별 키워드 조회
////    public List<SubTagDTO> getSubTagsByTag(int TagId) {
////        TagEntity TagEntity = tagRepository.findById(TagId)
////                .orElseThrow(()->new NotFoundException("Tag with ID " + TagId + " not found"));
////
////        List<SubTagEntity> subTagEntities = subTagRepository.findAllByTagEntity(TagEntity);
////        List<SubTagDTO> subTagDTOs = SubTagMapper.INSTANCE.subTagEntitiesToSubTagDTOs(subTagEntities);
////        return subTagDTOs;
////    }
//
//    // 키워드 추가
//    public SubTagDTO createSubTag(int TagId, SubTagBody subTagBody) {
//        TagEntity TagEntity = tagRepository.findById(TagId)
//                .orElseThrow(()->new NotFoundException("Tag with ID " + TagId + " not found"));
//
//        boolean subTagExists=subTagRepository.existsByTagEntityAndSubTag(TagEntity, subTagBody.getSubTag());
//            if(subTagExists){
//                throw new NotAcceptException("SubTag: " + subTagBody.getSubTag()+" is already added.");
//            }
//        SubTagEntity subTagEntity = SubTagMapper.INSTANCE.idAndSubTagBodyToSubTagEntity(null,subTagBody);
//        subTagEntity.setTagEntity(TagEntity);
//        SubTagEntity savedSubTag = subTagRepository.save(subTagEntity);
//        SubTagDTO subTagDTO = SubTagMapper.INSTANCE.subTagEntityToSubTagDTO(savedSubTag);
//        return subTagDTO;
//    }
//
//    // 키워드 여러개 추가
//    public List<SubTagDTO> createSubTags(int TagId, List<SubTagBody> subTagBodies) {
//        TagEntity TagEntity = tagRepository.findById(TagId)
//                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " not found"));
//
//        List<SubTagEntity> savedSubTagList = new ArrayList<>();
//
//        for (SubTagBody subTagBody : subTagBodies) {
//            boolean subTagExists = subTagRepository.existsByTagEntityAndSubTag(TagEntity, subTagBody.getSubTag());
//            if (subTagExists) {
//                throw new NotAcceptException("SubTag: " + subTagBody.getSubTag() + " is already added.");
//            }
//            SubTagEntity subTagEntity = SubTagMapper.INSTANCE.idAndSubTagBodyToSubTagEntity(null, subTagBody);
//            subTagEntity.setTagEntity(TagEntity);
//            SubTagEntity savedSubTag = subTagRepository.save(subTagEntity);
//            savedSubTagList.add(savedSubTag);
//
//        }List<SubTagDTO> subTagDTOs = SubTagMapper.INSTANCE.subTagEntitiesToSubTagDTOs(savedSubTagList);
//
//        return subTagDTOs;
//    }
//
//    //키워드 이름으로 카테고리 조회
//    public String findSubTag(SubTagBody subTagBody) {
//        List<SubTagEntity> existingSubTags =subTagRepository.findBySubTag(subTagBody.getSubTag());
//        if (existingSubTags.isEmpty()) {
//            throw new NotFoundException("No subTags found matching: " + subTagBody.getSubTag());
//        }
//        return existingSubTags.stream()
//                .map(subTag -> "SubTag: " + subTag.getSubTag() +
//                        ", Tag: " + subTag.getTagEntity().getTag() +
//                        ", Tag Id: " + subTag.getTagEntity().getTagId())
//                .collect(Collectors.joining("\n"));
//    }
//
//    // 키워드 삭제(키워드 SubTag로 조회)
//    public String deleteSubTag(int TagId, SubTagBody subTagBody) {
//        TagEntity TagEntity = tagRepository.findById(TagId)
//                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));
//
//        SubTagEntity existingSubTag =subTagRepository.findBySubTagAndTagEntity(subTagBody.getSubTag(), TagEntity)
//                .orElseThrow(() -> new NotFoundException("SubTag doesn't exist in the specified Tag"));
//            subTagRepository.delete(existingSubTag);
//            return "SubTag Id: " + existingSubTag.getSubTagId() + ", SubTag Name: " + existingSubTag.getSubTag() + " is deleted.";
//    }
//
//    // 키워드 삭제(키워드 Id로 조회)
//    public String deleteSubTagById(int TagId, int subTagId) {
//        TagEntity TagEntity = tagRepository.findById(TagId)
//                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));
//
//        SubTagEntity existingSubTag = subTagRepository.findBySubTagIdAndTagEntity(subTagId, TagEntity)
//                .orElseThrow(() -> new NotFoundException("SubTag doesn't exist in the specified Tag"));
//        subTagRepository.deleteById(existingSubTag.getSubTagId());
//        return String.format("SubTag Id: %d, SubTag Name: %s is deleted.", existingSubTag.getSubTagId(), existingSubTag.getSubTag());
//    }
//
//    //키워드 삭제 (해당 카테고리 내 전체 키워드 삭제)
//    public String deleteAllSubTagsByTag(int TagId) {
//        TagEntity TagEntity = tagRepository.findById(TagId)
//                .orElseThrow(() -> new NotFoundException("Tag with ID " + TagId + " doesn't exist"));
//
//        List<SubTagEntity> existingSubTags = subTagRepository.findAllByTagEntity(TagEntity);
//        if (existingSubTags.isEmpty()) {
//            throw new NotFoundException("No subTags found in the specified Tag");
//        }
//        subTagRepository.deleteAll(existingSubTags);
//        return "Deleted " + existingSubTags.size() + " subTags in Tag: " + TagEntity.getTag();
//    }
//
//    @Transactional
//    public Set<TagEntity> createTagsFromMatchedSubTags(List<String> matchedSubTags, QuestionEntity questionEntity) {
//        Set<TagEntity> tagEntities = new HashSet<>();
//
//        for (String subTag : matchedSubTags) {
//            TagEntity tagEntity = tagRepository.findByTag(subTag)
//                    .orElseGet(() -> {
//                        TagEntity newTag = new TagEntity();
//                        newTag.setTag(subTag.replaceAll("\\s+",""));
//                        return tagRepository.save(newTag);
//                    });
//
//            tagEntities.add(tagEntity);
//            tagEntity.getQuestions().add(questionEntity);
//        }
//
//        return tagEntities;
//    }
//}
//
//
//
