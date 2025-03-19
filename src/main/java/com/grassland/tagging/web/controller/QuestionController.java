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
    public QuestionDTO createQuestion(@RequestBody QuestionBody questionBody) {
        return questionService.createQuestion(questionBody);
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


    @Operation(summary = "Get one question by Id (with optional user timezone)")
    @GetMapping("/{questionId}")
    public QuestionDTO getQuestion(@PathVariable int questionId,
                                   @RequestParam(required = false) String userTimeZone) {
        if (userTimeZone != null) {
            return questionService.getQuestionById(questionId, userTimeZone);
        }
        return questionService.getQuestionById(questionId);
    }

    @Operation(summary = "Delete a question")
    @DeleteMapping("/{questionId}")
    public String deleteQuestion(@PathVariable int questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @Operation(summary = "Delete a question")
    @DeleteMapping()
    public String deleteAllQuestion() {
        return questionService.deleteAllQuestion();
    }
}