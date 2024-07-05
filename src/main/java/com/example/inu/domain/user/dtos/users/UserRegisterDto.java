package com.example.inu.domain.user.dtos.users;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data //getter/setter, @NoArgsConstructor까지 대체
public class UserRegisterDto {

    @NotEmpty(message = "name cannot be empty")
    private String name;
    @NotEmpty(message = "Email cannot be empty")
    private String email;;
    private String password;
}
//회원가입 DTO
//DTO를 거쳐서 User 객체에 주입하는 것.