package com.grassland.tagging.web.controller;
import com.grassland.tagging.service.OpenKoreanTextService;
import com.grassland.tagging.web.dto.QuestionBody;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class OpenKoreanTextController {

    private final OpenKoreanTextService openKoreanTextService;


    @GetMapping("/normalize")
    public String normalize(@RequestParam String text) {
        return openKoreanTextService.normalizeText(text);
    }

    @GetMapping("/tokenize")
    public List<String> tokenize(@RequestParam String text) {
        return openKoreanTextService.tokenizeText(text);
    }

    @GetMapping("/extract-nouns")
    public Set<String> extractTokens(@RequestBody QuestionBody questionBody) {
        return openKoreanTextService.extractNouns(questionBody);
    }

    @GetMapping("/extract-phrases")
    public Set<String> extractPhrases(@RequestBody QuestionBody questionBody) {
        return openKoreanTextService.extractPhrases(questionBody);
    }


}
