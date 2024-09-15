package com.example.inu.domain.recruitments.dtos.submissions;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SubmissionReadDto {
    private List<String> profiles;
    private List<Long> submissionIds;
    private List<Long> userIds;
    private List<String> userNames;
}