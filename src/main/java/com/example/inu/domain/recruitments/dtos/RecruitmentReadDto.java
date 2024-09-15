package com.example.inu.domain.recruitments.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RecruitmentReadDto {
    private Long id;
    private String title;
    private String type;
    private Integer number;
    private Date startDate;
    private Date endDate;
    private Date deadline;
    private Boolean closing;
    private List<String> positions;
    private List<String> techStacks;
    private Long userId;
}
//게시글 전체조회