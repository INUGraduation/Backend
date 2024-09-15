package com.example.inu.domain.recruitments.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RecruitmentDetailDto {
    private Long id;
    private String title;
    private String type;
    private Integer number;
    private Date createdDate;
    private Date startDate;
    private Date deadline;
    private Boolean closing;
    private String introduction;
    private List<String> positions;
    private List<String> techStacks;
    private Long userId;
    private String photo;
    private String name;
    private Long applicationId;


}
