package com.grassland.tagging.service;

import com.grassland.tagging.repository.QuestionRepository;
import com.grassland.tagging.repository.TagRepository;
import com.grassland.tagging.repository.entity.QuestionEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.service.exceptions.NotAcceptException;
import com.grassland.tagging.service.mapper.TagMapper;
import com.grassland.tagging.web.dto.TagBody;
import com.grassland.tagging.web.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TagService.class);


    public String createTag(TagBody tagBody) {
        boolean tagExists = tagRepository.existsByTag(tagBody.getTag());
        if (tagExists) {
            throw new NotAcceptException("Tag with the same name: " + tagBody.getTag() + " already exists.");
        }

        TagEntity tagEntity = TagMapper.INSTANCE.idAndTagBodyToTagEntity(null, tagBody);
        TagEntity tagCreated = tagRepository.save(tagEntity);
        return tagCreated.getTag();
    }

//    public List<String> createTags(List<TagBody> tagBodies) {
//        for (TagBody tagBody : tagBodies) {
//            if (tagRepository.existsByTag(tagBody.getTag())) {
//                throw new NotAcceptException("Tag with the same name already exists: " + tagBody.getName());
//            }
//        }
//        List<TagEntity> tagEntities = tagBodies.stream()
//                .map(tagBody -> TagMapper.INSTANCE.idAndTagBodyToTagEntity(null, tagBody))
//                .collect(Collectors.toList());
//        List<TagEntity> savedCategories = tagRepository.saveAll(tagEntities);
//
//        return savedCategories.stream()
//                .map(TagEntity::getTag)
//                .collect(Collectors.toList());
//    }

    //질문과 연관된 전체 태그 반환
    public String getTagsByQuestionId(int questionId) {
        Optional<QuestionEntity> question = questionRepository.findById(questionId);

        if (question.isPresent()) {
            Set<TagEntity> tags = question.get().getTags();

            return "Tags: " + tags.stream()
                    .map(TagEntity::getTag)
                    .collect(Collectors.joining(", "));
        } else {
            return "No question found for ID: " + questionId;
        }
    }

    //태그와 연관된 전체 질문 리스트 반환
    @Transactional
    public String getQuestionsByTagId(int tagId) {
        Optional<TagEntity> tag = tagRepository.findById(tagId);
        if (tag.isPresent()) {
            Set<QuestionEntity> questions = tag.get().getQuestions();
            // 질문 목록을 쉼표로 구분하여 반환
            return "Tag: " + tag.get().getTag() +"\n"+
                    "Questions related to this tag: \n" + questions
                    .stream()
                    .map(QuestionEntity::getQuestion)
                    .collect(Collectors.joining("\n"));
        } else {
            return "No tag found for ID: " + tagId;
        }
    }


    //전체 태그 반환 - 태그에 포함된 질문 수 내림차순 정렬
    //태그에 포함된 질문이 많아지면 나중에 해당 태그를 키워드로 넣을 수 있도록..!?
    public String getAllTags() {
        List<TagEntity> tagEntities = tagRepository.findAll();
        List<TagDTO> tagDTOs = TagMapper.INSTANCE.tagEntitiesToTagDTOs(tagEntities);
        tagDTOs.sort(Comparator.comparingInt(TagDTO::getQuestionCount).reversed());

        return "Tags: " + getTagNames(tagDTOs);
    }
    private String getTagNames(List<TagDTO> tagDTOList) {
        return tagDTOList.stream()
                .map(tagDTO -> "Id:"+tagDTO.getTagId() +"-"+tagDTO.getTag() + "(" + tagDTO.getQuestionCount() + "questions) ")
                .collect(Collectors.joining(", "));
    }

    public String deleteTagById(int tagId) {
        tagRepository.deleteById(tagId);
        return "Tag Id: "+ tagId + " is deleted";
    }

    public String deleteAllTags() {
        tagRepository.deleteAll();
        return "All tags are deleted.";
    }
}
