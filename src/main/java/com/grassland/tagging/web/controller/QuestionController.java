package com.grassland.tagging.web.controller;

import com.grassland.tagging.service.QuestionService;
import com.grassland.tagging.web.dto.QuestionBody;
import com.grassland.tagging.web.dto.QuestionDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/questions")
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Create a question")
    @PostMapping
    public String createQuestion(@RequestBody QuestionBody questionBody) {
        String createdQuestion = questionService.createQuestion(questionBody);
        return createdQuestion;
    }

//    @Operation(summary = "Create many questions")
//    @PostMapping("/batch")
//    public String createQuestion(@RequestBody List<QuestionBody> questionBodies) {
//        String createdQuestions = questionService.createQuestions(questionBodies);
//        return createdQuestions;
//    }

    @Operation(summary = "Get all questions")
    @GetMapping
    public List<QuestionDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @Operation(summary = "Get one question by Id")
    @GetMapping("/{questionId}")
    public QuestionDTO getQuestion(@PathVariable int questionId) {
        QuestionDTO questionDTO = questionService.getQuestionById(questionId);
        return questionDTO;
    }
//
//    @Operation(summary = "Get one question by Id + 사용자 시간대 맞춰 조회 가능하도록 함")
//    @GetMapping("/{questionId}")
//    public QuestionDTO getQuestion(@PathVariable int questionId, @RequestParam String userTimeZone ) {
//        QuestionDTO questionDTO = questionService.getQuestionById(questionId, userTimeZone);
//        return questionDTO;
//    }

    @Operation(summary = "Delete a question")
    @DeleteMapping("/{questionId}")
    public String deleteQuestion(@PathVariable int questionId) {
        String deletion = questionService.deleteQuestion(questionId);
        return deletion;
    }

    @Operation(summary = "Delete a question")
    @DeleteMapping()
    public String deleteAllQuestion() {
        String deletion = questionService.deleteAllQuestion();
        return deletion;
    }
}