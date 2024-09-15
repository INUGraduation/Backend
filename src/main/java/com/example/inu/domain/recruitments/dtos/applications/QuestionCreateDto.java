package com.example.inu.domain.recruitments.dtos.applications;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCreateDto {
    private String type;
    private String title;
    private List<String> options;
}