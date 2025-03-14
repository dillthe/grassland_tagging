package com.grassland.tagging.web.controller;

import com.grassland.tagging.service.TagService;
import com.grassland.tagging.web.dto.TagBody;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    //태그 등록
    @Operation(summary="Create a new tag")
    @PostMapping
    public String createTag(@RequestBody TagBody tagBody){
        String tag = tagService.createTag(tagBody);
        return "New tag: [" + tag + "] is created";
    }

    @Operation(summary="Create many tags")
    @PostMapping("/batch")
    public String createTags(@RequestBody List<TagBody> tagBodies){
        List<String> tagList = tagService.createTags(tagBodies);
        return "New tags: [" + tagList + "] are created";
    }


    // 태그 조회 (질문에 포함된 태그들 조회)
    @Operation(summary = "Get Tags by Question ID")
    @GetMapping("/question/{questionId}")
    public String getTagsByQuestionId(@PathVariable int questionId) {
        return tagService.getTagsByQuestionId(questionId);
    }

    @Operation(summary = "Get Questions by Tag Id")
    @GetMapping("/{tagId}")
    public String getQuestionsByTagId(@PathVariable int tagId) {
        return tagService.getQuestionsByTagId(tagId);
    }

    @Operation(summary = "Get All Tags")
    @GetMapping
    public String getAllTags() {
        return tagService.getAllTags();
    }

    @Operation(summary = "Delete Tag By Id")
    @DeleteMapping("/{tagId}")
    public String deleteTagById(@PathVariable int tagId) {
        return tagService.deleteTagById(tagId);
    }
//
//    @Operation(summary = "Delete Tag By Tag Name")
//    @DeleteMapping("/tagName/")
//    public String deleteTagByName(@RequestBody TagBody tagName) {
//        return tagService.deleteTagByName(tagName);
//    }

    @Operation(summary = "Delete All Tags")
    @DeleteMapping
    public String deleteAllTags() {
        return tagService.deleteAllTags();
    }
}
