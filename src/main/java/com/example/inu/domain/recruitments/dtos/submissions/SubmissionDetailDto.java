package com.example.inu.domain.recruitments.dtos.submissions;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmissionDetailDto {
    private Long userId;
    private String status;
    private List<AnswerReadDto> answers;
}