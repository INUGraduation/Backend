package com.example.inu.domain.recruitments.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RecruitmentUpdateDto {
    private String type;
    private String title;
    private Integer number;
    private Date startDate;
    private Date endDate;
    private Date deadline;
    private Boolean closing;

    private List<Long> positionIds;    // 포지션 ID 목록
    private List<Long> techStackIds;  // 테크스택 ID 목록
}
//게시글 수정 dto