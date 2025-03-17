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

    @Operation(summary="Get all subtags")
    @GetMapping("/subtag")
    public List<SubtagDTO> getAllSubtags() {
        return subtagService.getAllSubtags();
    }


    @Operation(summary="Get All Subtags by a TagID")
    @GetMapping("/tag/{tagId}/subtag")
    public List<SubtagDTO> getSubtagsByTagId(@PathVariable int tagId) {
        return  subtagService.getSubtagsByTagId(tagId);
    }

    @Operation(summary="Add a Subtag to Tag")
    @PostMapping("/tag/{tagId}/subtag")
    public String createSubtag(@PathVariable int tagId, @RequestBody SubtagBody subtagBody) {
        SubtagDTO subtagDTO = subtagService.createSubtag(tagId, subtagBody);
        return subtagDTO + "is successfully added!";
    }

    @Operation(summary="Add many subtags")
    @PostMapping("/tag/{tagId}/subtag/batch")
    public String createSubtags(@PathVariable int tagId, @RequestBody List<SubtagBody> subtagBodies) {
        List<SubtagDTO> subtagDTOs = subtagService.createSubtags(tagId, subtagBodies);
        return "Subtags are added successfully!" + subtagDTOs.toString();
    }

    //서브태그가 포함된 전체 태그 조회
    //상위태그-하위태그 다대다 관계로 동일한 하위태그가 여러 상위태그에 포함될 수 있음.
    //하위태그와 이게 포함된 상위태그가 함께 출력되어 한눈에 중복된 데이터를 조회 가능
    // 아래 delete a subtag by subtag 메소드 사용하면 삭제 가능
    @Operation(summary="Find subtags and their Tags(find duplicate subtags")
    @GetMapping("/subtag/find")
    public String findSubtags(@RequestBody SubtagBody subtagBody) {
        return subtagService.findSubtag(subtagBody);
    }

    // 하위태그 명으로 삭제
    @Operation(summary="Delete a Subtag by Subtag Id")
    @DeleteMapping("/subtag/{subtagId}")
    public String deleteSubtag(@PathVariable int subtagId, @RequestBody SubtagBody subtagBody) {
        return subtagService.deleteSubtag(subtagId, subtagBody);
    }

    //하위태그 Id로 삭제
    @Operation(summary="Delete a subtag by tagId")
    @DeleteMapping("/{tagId}/{subtagId}")
    public String deleteSubtag(@PathVariable int tagId, @PathVariable int subtagId) {
        String deletion = subtagService.deleteSubtagByTagId(tagId, subtagId);
        return deletion;
    }

    //상위태그 내 전체 하위 태그 삭제
    @Operation(summary="Delete all subtags by Tag")
    @DeleteMapping("/all/{tagId}")
    public String deleteSubtag(@PathVariable int tagId) {
        String deletion = subtagService.deleteAllSubtagsByTagId(tagId);
        return deletion;
    }

}
