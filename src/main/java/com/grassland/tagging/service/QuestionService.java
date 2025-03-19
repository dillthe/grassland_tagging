package com.grassland.tagging.service;


import com.grassland.tagging.repository.QuestionRepository;
import com.grassland.tagging.repository.SubtagRepository;
import com.grassland.tagging.repository.TagRepository;
import com.grassland.tagging.repository.entity.QuestionEntity;
import com.grassland.tagging.repository.entity.SubtagEntity;
import com.grassland.tagging.repository.entity.TagEntity;
import com.grassland.tagging.service.exceptions.NotAcceptException;
import com.grassland.tagging.service.exceptions.NotFoundException;
import com.grassland.tagging.service.mapper.QuestionMapper;
import com.grassland.tagging.web.dto.QuestionBody;
import com.grassland.tagging.web.dto.QuestionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final SubtagRepository subtagRepository;
    private final OpenKoreanTextService oktService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);


    @Transactional
    public String createQuestion(QuestionBody questionBody) {
        QuestionEntity questionEntity = QuestionMapper.INSTANCE.idAndQuestionBodyToQuestionEntity(null, questionBody);

        //토큰화 하기
        List<String> tagList = oktService.tokenizeText(questionBody);
        //구문 추출
//        Set<String> tagList = oktService.extractPhrases(questionBody);
        //명사 추출
//        Set<String> tagList = oktService.extractNouns(questionBody);

        Set<TagEntity> tagEntities = new HashSet<>();
        List<SubtagEntity> subtagEntities = new ArrayList<>();

        //모든 하위태그를 가져와서 List로 변환
        List<String> validSubtags = subtagRepository.findAll()
                .stream()
                .map(SubtagEntity::getSubtagName) //하위 태그 이름만 추출
                .collect(Collectors.toList());

        //하위태그가 포함된 태그를 필터링해서 matchedTag 생성
        List<String> matchedTags = tagList.stream()
                .map(tag -> (tag == null || tag.trim().isEmpty()) ? "기타" : tag.trim())
                .filter(tag -> validSubtags.stream().anyMatch(validSubtag -> tag.contains(validSubtag)))  // 하위 태그 체크
                .collect(Collectors.toList());
        if (matchedTags.isEmpty()) {
            matchedTags.add("기타");
        }
        logger.info("Matched tags: " + matchedTags);

        // 2. matchedTags를 돌면서 해당하는 TagEntity를 추가
        for (String subtag : matchedTags) {
            SubtagEntity subtagEntity = findSubtagEntity(subtag);  // 태그에 해당하는 TagEntity 찾기

            // 디버깅 로그
            if (subtagEntity != null) {
                logger.info("Matched TagEntity: " + subtagEntity.getTags());
            } else {
                logger.info("No matching TagEntity found for: " + subtag);
            }

            // 태그가 없으면 "기타" 태그로 대체
            if (subtagEntity == null) {
                subtagEntity = subtagRepository.findBySubtagName("기타").orElseGet(() -> createOtherSubtag());  // "기타" 태그 없으면 새로 생성
            }

            // QuestionEntity에 태그 추가
            questionEntity.getSubtags().add(subtagEntity);
            subtagEntities.add(subtagEntity);
            questionEntity.getTags().addAll(subtagEntity.getTags());
            tagEntities.addAll(subtagEntity.getTags());
            subtagEntity.getQuestions().add(questionEntity);
        }

        // QuestionEntity 저장
        questionEntity.setTags(tagEntities);
        questionEntity.setSubtags(subtagEntities);
        QuestionEntity savedQuestion = questionRepository.save(questionEntity);
        QuestionDTO questionDTO = QuestionMapper.INSTANCE.questionEntityToQuestionDTO(savedQuestion);

        // 결과 반환
        return String.format("Question is created: Q.ID:%s, %s, Tags: %s, Subtags: %s",
                questionDTO.getQuestionId(),
                questionDTO.getQuestion(),
                questionDTO.getTags(),
                questionDTO.getSubtags(),
                String.join(", "));
    }
    private SubtagEntity findSubtagEntity(String text) {
        // 하위 태그에서 찾기
        SubtagEntity subtagEntity = subtagRepository.findAll()
                .stream()
                .filter(s -> text.contains(s.getSubtagName()))  // 하위 태그 부분 문자열 포함 검사
                .findFirst()
                .orElse(null);

        // 하위 태그가 존재하면, 그 하위 태그에 연결된 상위 태그를 반환
        if (subtagEntity != null) {
            return subtagEntity;  // 하위 태그에 연결된 상위 태그를 반환
        }

        // 하위 태그가 없으면 null 반환
        return null;
    }

    // "기타" 태그가 없으면 새로 생성하는 메서드
    private SubtagEntity createOtherSubtag() {
        SubtagEntity otherTag = new SubtagEntity();
        otherTag.setSubtagName("기타");
        return subtagRepository.save(otherTag);  // 새로 생성하여 저장
    }

    //전체 질문 조회
    public List<QuestionDTO> getAllQuestions() {
        List<QuestionEntity> questionEntities = questionRepository.findAll();
        List<QuestionDTO> questionDTOs = QuestionMapper.INSTANCE.questionEntitiesToQuestionDTOs(questionEntities);
        return questionDTOs;
    }

    //    질문 단건 조회
    public QuestionDTO getQuestionById(int questionId) {
        QuestionEntity existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotAcceptException("Question Id doesn't exist"));
        QuestionDTO questionDTO = QuestionMapper.INSTANCE.questionEntityToQuestionDTO(existingQuestion);
        return questionDTO;
    }

    public QuestionDTO getQuestionById(int questionId, String userTimeZone) {
        QuestionEntity existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotAcceptException("Question Id doesn't exist"));
        QuestionDTO questionDTO = QuestionMapper.INSTANCE.questionEntityToQuestionDTO(existingQuestion);
        //질문을 저장할 때는 UTC타임으로 저장이 되지만, 질문을 조회할 때는 사용자 위치에 맞게 시간대가 변환되어 조회됨.
        String formattedTime = TimeZoneConverter.convertToUserTimeZone(existingQuestion.getCreatedAt(), userTimeZone);
        questionDTO.setCreatedAt(formattedTime);
        return questionDTO;
    }


    public String deleteQuestion(int questionId) {
        QuestionEntity existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("No Question found for this Id" + questionId));
        questionRepository.deleteById(existingQuestion.getQuestionId());
        return String.format("Question Id: %d, Question: %s is deleted.", existingQuestion.getQuestionId(), existingQuestion.getQuestion());
    }

    public String deleteAllQuestion() {
        questionRepository.deleteAll();
        return "All questions are deleted.";
    }


    //////////////////질문 여러개 한꺼번에 등록
//    @Transactional
//    public String createQuestions(List<QuestionBody> questionBodies) {
//        StringBuilder result = new StringBuilder();
//
//        for (QuestionBody questionBody : questionBodies) {
//            QuestionEntity questionEntity = QuestionMapper.INSTANCE.idAndQuestionBodyToQuestionEntity(null, questionBody);
//
//            // 질문에서 키워드를 뽑아 카테고리 결정
//            String category = categoryService.determineCategory(questionBody.getQuestion());
//            logger.info("Determined category: {}", category);
//            //연관된 카테고리 이름 여러개일 경우 split
//            String[] categoryNames = category.split(",");
//            Set<TagEntity> tagEntities = new HashSet<>();
//            // 매칭된 키워드 목록 가져오기
//            List<String> matchedKeywords = categoryService.getMatchedKeywords(questionBody.getQuestion());
//
//
//            for (String categoryName : categoryNames) {
//                String trimmedCategoryName = (categoryName == null || categoryName.trim().isEmpty()) ? "기타" : categoryName.trim();
//
//                // 카테고리 조회
//                CategoryEntity categoryEntity = categoryRepository.findByName(trimmedCategoryName).orElse(null);
//
//                if (categoryEntity != null) {
//                    questionEntity.setCategoryEntity(categoryEntity);
//                    logger.info("Category assigned. Category: {}, Keywords:{}", categoryEntity.getName(), matchedKeywords);
//                } else {
//                    logger.warn("CategoryEntity is null. Saving as tag: {}", trimmedCategoryName);
//                }
//
//                // 태그 조회 및 생성 (람다식 내에서 사용될 변수는 effectively final 이어야 함)
//                TagEntity tagEntity = tagRepository.findByTag(trimmedCategoryName)
//                        .orElseGet(() -> {
//                            TagEntity newTag = new TagEntity();
//                            newTag.setTag(trimmedCategoryName.replaceAll(" ", ""));
//                            return tagRepository.save(newTag);
//                        });
//
//                tagEntities.add(tagEntity);
//                tagEntity.getQuestions().add(questionEntity);
//            }
//
//            //매칭된 키워드가 있다면 이것도 태그로 저장
//            tagEntities.addAll(keywordService.createTagsFromMatchedKeywords(matchedKeywords, questionEntity));
//
//            questionEntity.setTags(tagEntities);
//            QuestionEntity savedQuestion = questionRepository.save(questionEntity);
//
//            QuestionDTO questionDTO = QuestionMapper.INSTANCE.questionEntityToQuestionDTO(savedQuestion);
//
//
//            return String.format("Question is created: %s, Category: %s, Keyword : %s, Tags: %s",
//                    questionDTO.getQuestion(),
//                    questionDTO.getCategoryName(),
//                    categoryService.getMatchedKeywords(questionDTO.getQuestion()),
//                    String.join(", ", categoryNames));
//        }
//        return result.toString();
//    }
}