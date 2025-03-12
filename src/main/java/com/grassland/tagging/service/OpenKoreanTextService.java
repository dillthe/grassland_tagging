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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenKoreanTextService {

    private static final Logger logger = LoggerFactory.getLogger(OpenKoreanTextService.class);

    public String normalizeText(String text) {
        return OpenKoreanTextProcessorJava.normalize(text).toString();
    }

    public List<String> tokenizeText(String text) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        return OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);
    }

    // 필터링할 단어 리스트
    List<String> filterWords = Arrays.asList("대한", "지금", "아니면", "이제", "거", "내", "로만", "것", "이런", "저런", "어떤", "종종", "과연", "나", "오늘", "그" );

    public List<String> extractNouns(QuestionBody questionBody) {

    // 텍스트를 토큰화합니다.
    Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(questionBody.getQuestion());

    // 토큰 중에서 명사만 필터링하여 리스트로 반환
    Set<String> filteredNouns = JavaConverters.seqAsJavaList(tokens).stream()
            .filter(token -> token.pos().equals(KoreanPos.Noun()))  // 명사(Noun)만 필터링
            .map(KoreanTokenizer.KoreanToken::text)
            .collect(Collectors.toSet());  // 중복 제거

    log.info("filteredPhrases" + filteredNouns);  // 필터링된 명사 리스트 확인

    // 필터링한 구문들 중에서 추가 필터링
    List<String> result = filteredNouns.stream()
            .filter(noun -> filterWords.stream().noneMatch(noun::contains)) // 필터리스트의 단어가 포함되지 않은 경우만
            .collect(Collectors.toList());

    log.info("result" + result);  // 필터링된 결과

    // 짧은 구문이 긴 구문에 포함되는 경우 긴 구문만 남기도록 필터링
    List<String> finalResult = result;
    result = result.stream()
            .filter(noun -> finalResult.stream().noneMatch(other -> other.contains(noun) && !other.equals(noun)))
            .collect(Collectors.toList());

    return result;
    }


    public List<String> extractPhrases(QuestionBody questionBody) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(questionBody.getQuestion());

        List<KoreanPhraseExtractor.KoreanPhrase> phrases = OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true);
        log.info("tokens:" + tokens);
        log.info("phrases:" + phrases);

        // 필터링 후 중복 제거
        Set<String> filteredPhrases = phrases.stream()
                .map(KoreanPhraseExtractor.KoreanPhrase::text)
                .filter(phrase -> filterWords.stream().noneMatch(phrase::contains)) // 필터리스트의 단어가 포함되지 않은 경우만
                .collect(Collectors.toSet());  // 중복을 제거하기 위해 Set 사용
        log.info("filteredPhrases" + filteredPhrases);
        // 중복된 구문을 필터링한 뒤, 결과를 리스트로 반환

        Set<String> filteredNouns = JavaConverters.seqAsJavaList(tokens).stream()
                .filter(token -> token.pos().equals(KoreanPos.Noun()))  // 명사(Noun)만 필터링
                .map(KoreanTokenizer.KoreanToken::text)
                .collect(Collectors.toSet());  // 중복 제거
        log.info("filteredNoun"+filteredNouns);
        //**********코드 다시 짜기 : filtered Noun에서 나온 값을 기준으로 태그를 검색해서, 태그에 해당되는게 있는 경우에만
        //그 태그를 반환하도록 하고, filteredPhrases랑은 아래처럼 병합은 안 될 것 같음.
        //[ "취업","나","안","왜" 이런식으로 나옴. 만약 병합 안하면 그냥 "취업"만 나옴.
        //'술'관련 질문에서 '술' '죄' 뭐 이런 한글자 단어가 filteredPhrases로 반환되지 않는 문제때문에 만든건데
        //태그 코드를 더 구체화 한 다음 다시 수정해봐야 될 것 같음.

//        // filteredPhrases와 filteredNoun을 병합
//        Set<String> combinedFiltered = Stream.concat(filteredPhrases.stream(), filteredNouns.stream())
//                .collect(Collectors.toSet());
//
//        log.info("combinedFiltered: " + combinedFiltered);


        List<String> result = new ArrayList<>(filteredPhrases);
        log.info("result" + result);
        // 짧은 구문이 긴 구문에 포함되는 경우 긴 구문만 남기도록 필터링
        List<String> finalResult = result;
        result = result.stream()
                .filter(phrase -> finalResult.stream().noneMatch(other -> other.contains(phrase) && !other.equals(phrase)))
                .collect(Collectors.toList());

        return result;
    }
}
