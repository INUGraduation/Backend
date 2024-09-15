package com.example.inu.domain.recruitments.dtos.applications;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationReadDto {
    private Long applicationId;
    private List<QuestionReadDto> questions;

}
