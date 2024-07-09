package com.example.inu.domain.user.dtos.profiles;


import lombok.Data;
import java.util.List;

//프로필 조회 dto
@Data
public class ProfileViewDto {
    private String name;
    private Long id;
    private String photo;
    private String gender;
    private String intro;
    private String residence;
    private String status;
    private List<String> positions;
    private List<String> techStacks;
    private String github;

}