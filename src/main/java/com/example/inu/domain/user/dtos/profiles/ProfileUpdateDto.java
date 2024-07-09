package com.example.inu.domain.user.dtos.profiles;



import lombok.Data;
import java.util.List;
//profile 수정 dto
@Data
public class ProfileUpdateDto {
    private String photo;
    private String gender;
    private String intro;
    private String residence;
    private String status;
    private List<String> positions;
    private List<String> techStacks;
    private String github;
}