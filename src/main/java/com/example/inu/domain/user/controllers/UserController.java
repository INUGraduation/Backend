package com.example.inu.domain.user.controllers;


import com.example.inu.domain.user.dtos.ResponseDto;
import com.example.inu.domain.user.dtos.users.EmailCheckDto;
import com.example.inu.domain.user.dtos.users.UserLoginDto;
import com.example.inu.domain.user.dtos.users.UserRegisterDto;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.services.UserService;
import com.example.inu.global.jwt.TokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RequestMapping("/api/v1/auth")
@RestController//@RestController는 @Controller에 @ResponseBody가 추가된 것입니다.
// 당연하게도 RestController의 주용도는 Json 형태로 객체 데이터를 반환하는 것
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, TokenProvider tokenProvider){
        this.userService = userService;
        this.tokenProvider=tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegisterDto userDto) {
        User registeredUser = userService.registerUser(userDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        Authentication authentication = userService.getAuthentication(userLoginDto);
        System.out.println(authentication);
        if (authentication != null && authentication.isAuthenticated()) {
            String accessToken = tokenProvider.createToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication.getName());  // Refresh Token 생성
            System.out.println(authentication.getName());
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);

            return ResponseEntity.ok(tokens);

        }
        return ResponseEntity.status(401).body("Login failed");
    }
//    @PostMapping("auth/login")
//    public ResponseEntity<User> login(@RequestBody UserLoginDto userLoginDto){
//}

    @PostMapping("/check")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckDto emailCheckDto){
        boolean isAvailable = userService.checkEmailAvailability(emailCheckDto.getEmail());
        if(isAvailable){
            return ResponseEntity.ok(new ResponseDto("success","사용 가능한 이메일입니다."));

        }
        else {
            return ResponseEntity.ok(new ResponseDto("error","사용 중인 이메일입니다."));
        }
    }



    @GetMapping("/hello")//test 성공;
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

}