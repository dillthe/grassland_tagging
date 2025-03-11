package com.grassland.tagging.web.controller;
import com.grassland.tagging.service.OpenKoreanTextService;
import com.grassland.tagging.web.dto.QuestionBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class OpenKoreanTextController {

    private final OpenKoreanTextService openKoreanTextService;

    @GetMapping("/extract")
    public List<String> extractKeyword(@RequestBody QuestionBody questionBody){
        List<String> tag = openKoreanTextService.extractKeywords(questionBody.getQuestion());
        return Collections.singletonList("질문하신 내용과 관련된 태그는 " + tag.toString() + "입니다.");
    }

    @GetMapping("/normalize")
    public String normalize(@RequestParam String text) {
        return openKoreanTextService.normalizeText(text);
    }

    @GetMapping("/tokenize")
    public List<String> tokenize(@RequestParam String text) {
        return openKoreanTextService.tokenizeText(text);
    }

    @GetMapping("/extract-phrases")
    public List<String> extractPhrases(@RequestParam String text) {
        return openKoreanTextService.extractPhrases(text);
    }


}
