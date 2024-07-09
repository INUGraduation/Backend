package com.example.inu.domain.user.dtos.profiles;


import lombok.Data;
import java.util.List;

@Data
public class ProfileDto {
    private String gender;
    private String intro;
    private String residence;
    private String status;
    private List<Long> positionIds;    // 포지션 ID 목록
    private List<Long> techStackIds;
    private String github;



}