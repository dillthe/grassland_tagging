package com.grassland.tagging.service;

import com.grassland.tagging.web.dto.QuestionBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.openkoreantext.processor.util.KoreanPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenKoreanTextService {

    private static final Logger logger = LoggerFactory.getLogger(OpenKoreanTextService.class);

    public String normalizeText(QuestionBody questionBody) {
        return OpenKoreanTextProcessorJava.normalize(questionBody.getQuestion()).toString();
    }

    public List<String> tokenizeText(QuestionBody questionBody) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(questionBody.getQuestion());
        log.info("tokens: " + tokens);
        return OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);
    }


    // 필터링할 단어 리스트 (명사추출, 구문추출 시 사용)
    List<String> filterWords = Arrays.asList("대한", "지금", "아니면", "이제", "거", "내", "로만", "것", "이런", "저런", "어떤", "종종", "과연", "나", "오늘", "그");


    //명사추출
    public Set<String> extractNouns(QuestionBody questionBody) {
        // 텍스트를 토큰화
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(questionBody.getQuestion());

        // 명사(Noun)만 필터링하여 Set으로 저장 (중복 제거됨)
        Set<String> filteredNouns = JavaConverters.seqAsJavaList(tokens).stream()
                .filter(token -> token.pos().equals(KoreanPos.Noun()))  // 명사만 필터링
                .map(KoreanTokenizer.KoreanToken::text)
                .collect(Collectors.toSet());  // Set으로 변환하여 중복 제거

        log.info("filteredNouns: " + filteredNouns);

        // 필터링한 구문들 중에서 추가 필터링
        Set<String> result = filteredNouns.stream()
                .filter(noun -> filterWords.stream().noneMatch(noun::contains)) // 필터 리스트에 포함되지 않은 단어만 남김
                .collect(Collectors.toSet());

        log.info("result after filtering: " + result);

        // 짧은 구문이 긴 구문에 포함되는 경우 긴 구문만 남기도록 필터링
        Set<String> finalResult = result.stream()
                .filter(noun -> result.stream().noneMatch(other -> other.contains(noun) && !other.equals(noun)))
                .collect(Collectors.toSet());

        return finalResult;
    }


    public Set<String> extractPhrases(QuestionBody questionBody) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(questionBody.getQuestion());

        Set<KoreanPhraseExtractor.KoreanPhrase> phrases = new HashSet<>(OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true));
        log.info("tokens:" + tokens);
        log.info("phrases:" + phrases);
        return Collections.singleton(String.valueOf(phrases));

        // 필터링 후 중복 제거
//        Set<String> filteredPhrases = phrases.stream()
//                .map(KoreanPhraseExtractor.KoreanPhrase::text)
//                .filter(phrase -> filterWords.stream().noneMatch(phrase::contains)) // 필터리스트의 단어가 포함되지 않은 경우만
//                .collect(Collectors.toSet());  // 중복을 제거하기 위해 Set 사용
//        log.info("filteredPhrases" + filteredPhrases);
//        return filteredPhrases;
//        // 중복된 구문을 필터링한 뒤, 결과를 리스트로 반환
//
//        Set<String> filteredNouns = JavaConverters.seqAsJavaList(tokens).stream()
//                .filter(token -> token.pos().equals(KoreanPos.Noun()))  // 명사(Noun)만 필터링
//                .map(KoreanTokenizer.KoreanToken::text)
//                .collect(Collectors.toSet());  // 중복 제거
//        log.info("filteredNoun" + filteredNouns);
        //'술'관련 질문에서 '술' '죄' 뭐 이런 한글자 단어가 filteredPhrases로 반환되지 않는 문제때문에 non+phrases를 합쳐 만든 코드인데,
        //[ "취업","나","안","왜" 이런식으로 나옴. 만약 병합 안하면 그냥 "취업"만 나옴.
        //태그 코드를 더 구체화 한 다음 다시 수정해봐야 될 것 같음.

//        // filteredPhrases와 filteredNoun을 병합
//        Set<String> combinedFiltered = Stream.concat(filteredPhrases.stream(), filteredNouns.stream())
//                .collect(Collectors.toSet());
//
//        log.info("combinedFiltered: " + combinedFiltered);


//        Set<String> result = new HashSet<>(filteredPhrases);
//        log.info("result" + result);
//        // 짧은 구문이 긴 구문에 포함되는 경우 긴 구문만 남기도록 필터링
//        Set<String> finalResult = result;
//        result = result.stream()
//                .filter(phrase -> finalResult.stream().noneMatch(other -> other.contains(phrase) && !other.equals(phrase)))
//                .collect(Collectors.toSet());

//        return result;
    }
}
