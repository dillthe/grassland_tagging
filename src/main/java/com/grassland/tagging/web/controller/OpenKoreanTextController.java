package com.grassland.tagging.web.controller;

import com.grassland.tagging.service.OpenKoreanTextService;
import com.grassland.tagging.web.dto.QuestionBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class OpenKoreanTextController {

    private final OpenKoreanTextService openKoreanTextService;


    @GetMapping("/normalize")
    public String normalize(@RequestBody QuestionBody questionBody) {
        return openKoreanTextService.normalizeText(questionBody);
    }

    @GetMapping("/tokenize")
    public List<String> tokenize(@RequestBody QuestionBody questionBody) {
        return openKoreanTextService.tokenizeText(questionBody);
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
