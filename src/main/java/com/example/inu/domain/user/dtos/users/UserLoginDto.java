package com.example.inu.domain.user.dtos.users;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@Setter  Setter로 DTO 필드들에 주입을 하는 것이 아니라 reflection이라는 기능을 통해 값을 주입하므로 Setter는 딱히 필요가 없다.
@NoArgsConstructor
public class UserLoginDto {
    private String email;
    private String password;
}