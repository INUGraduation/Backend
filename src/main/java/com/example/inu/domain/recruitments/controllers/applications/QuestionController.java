package com.example.inu.domain.recruitments.controllers.applications;

import com.example.inu.domain.recruitments.dtos.applications.QuestionCreateDto;
import com.example.inu.domain.recruitments.entities.applications.Question;
import com.example.inu.domain.recruitments.services.applications.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 질문 생성
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreateDto dto, @PathVariable Long applicationId) {
        try {
            Question question = questionService.createQuestion(dto, applicationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("questionId", question.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
