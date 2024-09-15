package com.example.inu.domain.recruitments.dtos.submissions;



import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SubmissionStatusDto {
    private Long id;
    private String type;
    private String title;
    private Integer number;
    private Date startDate;
    private Date endDate;
    private Date deadline;

    @Getter
    private String status;

    private List<String> positions;
    private List<String> techStacks;

}