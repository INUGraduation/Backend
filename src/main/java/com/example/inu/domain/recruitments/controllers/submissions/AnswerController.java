package com.example.inu.domain.recruitments.controllers.submissions;

import com.example.inu.domain.recruitments.dtos.submissions.AnswerCreateDto;
import com.example.inu.domain.recruitments.entities.submissions.Answer;
import com.example.inu.domain.recruitments.services.submissions.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications/questions/{questionId}/submissions/{submissionId}/answers")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    // 답변 생성s
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createAnswer(@ModelAttribute AnswerCreateDto dto, @RequestParam(value ="file", required = false)MultipartFile file,
                                          @PathVariable Long questionId, @PathVariable Long submissionId) {
        try {
            Answer answer = answerService.createAnswer(dto, questionId, submissionId,file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("answerId", answer.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}