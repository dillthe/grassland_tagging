package com.grassland.tagging.web.controller;

import com.grassland.tagging.service.SubtagService;
import com.grassland.tagging.web.dto.SubtagBody;
import com.grassland.tagging.web.dto.SubtagDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class SubtagController {

    private final SubtagService subtagService;

    @Operation(summary="Add a subtag to tag")
    @PostMapping("/tag/{tagId}/subtag")
    public String createSubtag(@PathVariable int tagId, @RequestBody SubtagBody subtagBody) {
        SubtagDTO subtagDTO = subtagService.createSubtag(tagId, subtagBody);
        return subtagDTO + "is successfully added!";
    }


    @Operation(summary="Add multiple subtags")
    @PostMapping("/tag/{tagId}/subtag/batch")
    public String createSubtags(@PathVariable int tagId, @RequestBody List<SubtagBody> subtagBodies) {
        List<SubtagDTO> subtagDTOs = subtagService.createSubtags(tagId, subtagBodies);
        return "Subtags are added successfully!" + subtagDTOs;
    }


    //서브태그로 상위 태그 조회
    //상위태그-하위태그 다대다 관계로 동일한 하위태그가 여러 상위태그에 포함될 수 있음.
    //하위태그와 이게 포함된 상위태그가 함께 출력되어 한눈에 중복된 데이터를 조회 가능
    //아래 delete a subtag by subtag 메소드 사용하면 원하는 정보 손쉽게 삭제 가능
    @Operation(summary="Get all tags associated with a subtag")
    @GetMapping("/subtag/find")
    public String findSubtag(@RequestBody SubtagBody subtagBody) {
        return subtagService.findSubtag(subtagBody);
    }

    @Operation(summary="Get all subtags")
    @GetMapping("/subtag")
    public List<SubtagDTO> getAllSubtags() {
        return subtagService.getAllSubtags();
    }


    @Operation(summary="Get all subtags by a tagId")
    @GetMapping("/tag/{tagId}/subtag")
    public String getSubtagsByTagId(@PathVariable int tagId) {
        String subtagDTOs = subtagService.getSubtagsByTagId(tagId);
        return subtagDTOs;
    }

    @Operation(summary="Delete a subtag by subtag name")
    @DeleteMapping("/subtag")
    public String deleteSubtag(@RequestBody SubtagBody subtagBody) {
        return subtagService.deleteSubtag(subtagBody);
    }

    @Operation(summary="Delete a subtag by tagId")
    @DeleteMapping("/{tagId}/{subtagId}")
    public String deleteSubtag(@PathVariable int tagId, @PathVariable int subtagId) {
        String deletion = subtagService.deleteSubtagByTagId(tagId, subtagId);
        return deletion;
    }


    @Operation(summary="Delete all subtags ")
    @DeleteMapping("/subtag/all")
    public String deleteSubtag() {
        String deletion = subtagService.deleteAllSubtags();
        return deletion;
    }

}
