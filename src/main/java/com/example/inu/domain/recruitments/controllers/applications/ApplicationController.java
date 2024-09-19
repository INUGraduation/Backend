package com.example.inu.domain.recruitments.controllers.applications;


import com.example.inu.domain.recruitments.dtos.applications.ApplicationReadDto;
import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.services.applications.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recruitments/{recruitmentId}/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // 신청서 생성
    @PostMapping
    public ResponseEntity<?> createApplication(@PathVariable Long recruitmentId) {
        Application application = applicationService.createApplication(recruitmentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("applicationId", application.getId()));
    }

    // 신청서 조회
    @GetMapping
    public ResponseEntity<ApplicationReadDto> getApplication(@PathVariable Long recruitmentId) {
        return applicationService.getApplication(recruitmentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}