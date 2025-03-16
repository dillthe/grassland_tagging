//package com.grassland.tagging.web.controller;
//
//import com.grassland.tagging.service.SubTagService;
//import com.grassland.tagging.web.dto.SubTagBody;
//import com.grassland.tagging.web.dto.SubTagDTO;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//@RequestMapping("/api/subTags")
//@RequiredArgsConstructor
//@RestController
//public class SubTagController {
//
//    private final SubTagService subTagService;
//
//    @Operation(summary="Get all subTags")
//    @GetMapping
//    public List<SubTagDTO> getAllSubTags() {
//        return subTagService.getAllSubTags();
//    }
//
//
//    @Operation(summary="Get subTags by a category")
//    @GetMapping("/{categoryId}")
//    public List<SubTagDTO> getSubTagsByTag(@PathVariable int categoryId) {
//        return  subTagService.getSubTagsByTag(categoryId);
//    }
//
//    @Operation(summary="Add a subTag")
//    @PostMapping("/{categoryId}")
//    public String createSubTag(@PathVariable int categoryId, @RequestBody SubTagBody subTagBody) {
//        SubTagDTO subTagDTO = subTagService.createSubTag(categoryId, subTagBody);
//        return subTagDTO + "is successfully added!";
//    }
//
//    @Operation(summary="Add many subTags")
//    @PostMapping("/{categoryId}/batch")
//    public String createSubTags(@PathVariable int categoryId, @RequestBody List<SubTagBody> subTagBodies) {
//        List<SubTagDTO> subTagDTOs = subTagService.createSubTags(categoryId, subTagBodies);
//        return "SubTags are added successfully!" + subTagDTOs.toString();
//    }
//
//    //키워드가 있는 카테고리 전부 조회
//    //동일 키워드가 여러 카테고리에 들어있을 경우에 중복된 정보를 삭제하기 위함
//    //키워드명, 키워드가 포함된 카테고리 번호가 출력되면 여기서 필요없는 카테고리 번호로 아래 delete a subTag by subTag 메소드 사용하면 됨.
//    @Operation(summary="Find subTags and their categories(find duplicate subTags")
//    @GetMapping("/find")
//    public String findSubTags(@RequestBody SubTagBody subTagBody) {
//        return subTagService.findSubTag(subTagBody);
//    }
//
//    // 키워드 명으로 삭제
//    @Operation(summary="Delete a subTag by SubTag")
//    @DeleteMapping("/{categoryId}")
//    public String deleteSubTag(@PathVariable int categoryId, @RequestBody SubTagBody subTagBody) {
//        return subTagService.deleteSubTag(categoryId, subTagBody);
//    }
//
//    //키워드 id로 삭제
//    @Operation(summary="Delete a subTag by Id")
//    @DeleteMapping("/{categoryId}/{subTagId}")
//    public String deleteSubTag(@PathVariable int categoryId, @PathVariable int subTagId) {
//        String deletion = subTagService.deleteSubTagById(categoryId, subTagId);
//        return deletion;
//    }
//
//    //카테고리 내 전체 키워드 삭제
//    @Operation(summary="Delete all subTags by Tag")
//    @DeleteMapping("/all/{categoryId}")
//    public String deleteSubTag(@PathVariable int categoryId) {
//        String deletion = subTagService.deleteAllSubTagsByTag(categoryId);
//        return deletion;
//    }
//
//}
