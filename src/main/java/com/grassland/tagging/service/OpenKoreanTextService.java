package com.grassland.tagging.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenKoreanTextService {

    private static final Logger log = LoggerFactory.getLogger(OpenKoreanTextService.class);
    public List<String> extractKeywords(String text) {
        // 텍스트 토큰화
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        log.info("Tokens: " + tokens);

        // 구문 추출
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true);
        log.info("Phrases: " + phrases);

        // 명사만 필터링하고 빈도수 기준으로 중요 키워드 추출
        return extractImportantKeywords(phrases);
    }

    private List<String> extractImportantKeywords(List<KoreanPhraseExtractor.KoreanPhrase> phrases) {
        // 추출된 구에서 중요 키워드를 필터링
        Map<String, Integer> frequencyMap = new HashMap<>();

        // 명사들만 필터링하고 빈도수 기록
        for (KoreanPhraseExtractor.KoreanPhrase phrase : phrases) {
            String text = phrase.text();
            List<KoreanTokenizer.KoreanToken> tokens = (List<KoreanTokenizer.KoreanToken>) phrase.tokens();

            // 품사 필터링: 명사만 추출
            String keyword = tokens.stream()
                    .filter(token -> "Noun".equals(token.pos())) // 명사만 필터링
                    .map(KoreanTokenizer.KoreanToken::text)
                    .collect(Collectors.joining("")); // 명사를 이어붙여서 하나의 키워드로 만듦

            // 필터링된 키워드가 비어있지 않으면 빈도수를 기록
            if (!keyword.isEmpty()) {
                frequencyMap.put(keyword, frequencyMap.getOrDefault(keyword, 0) + 1);
            }
        }


        // 빈도수가 높은 순으로 정렬
        return frequencyMap.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


//        // Normalize text
//        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
//
//        // Tokenize text
//        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
//
//        // Extract phrases (구 추출)
//        List<KoreanPhraseExtractor.KoreanPhrase> phrases = OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true);
//
//        return phrases.stream()
//                .map(KoreanPhraseExtractor.KoreanPhrase::text) // 텍스트로 변환
//                .collect(Collectors.toList());
//    }


    public String normalizeText(String text) {
        return OpenKoreanTextProcessorJava.normalize(text).toString();
    }

    public List<String> tokenizeText(String text) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        return OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens);
    }

    public List<String> extractPhrases(String text) {
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(text);
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true);
        return phrases.stream().map(KoreanPhraseExtractor.KoreanPhrase::text).collect(Collectors.toList());
    }
}
