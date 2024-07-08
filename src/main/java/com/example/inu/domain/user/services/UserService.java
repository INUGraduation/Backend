package com.example.inu.domain.user.services;

import com.example.inu.domain.user.dtos.users.UserLoginDto;
import com.example.inu.domain.user.dtos.users.UserRegisterDto;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    //Java에서는 인터페이스 타입으로 변수를 선언할 수 있습니다.이러한 방법은 객체 지향 프로그래밍에서 다형성을 활용하는 데 중요한 역할을 함.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        User newUser = User.builder()
                .name(userRegisterDto.getName())
                .email(userRegisterDto.getEmail())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .build();
        return userRepository.save(newUser);
    }

    public Authentication getAuthentication(UserLoginDto userLoginDto) {//로그인
        User user = userRepository.findByEmail(userLoginDto.getEmail()).orElse(null);
        if (user == null) {
            System.out.println("User not found with email: " + userLoginDto.getEmail());
            return null;
        }
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            System.out.println("Password mismatch for user: " + user.getEmail());
            return null;
        }
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), Collections.singletonList(authority));
        //UsernamePasswordAuthenticationToken 생성: 이 객체는 여러 생성자를 가지고 있으며, 권한 목록이 포함된 생성자를 사용할 때 authenticated 속성이 true로 설정됩니다
        //이래서 authenticated가 false->true로 설정된 것.

        return auth;
    }

    public boolean checkEmailAvailability(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public User findUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
    public User updateUser(Long id, UserRegisterDto userDto){
        return userRepository.findById(id).map(user-> {user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            return userRepository.save(user);
        }).orElse(null);

    }

    public boolean deleteUser(Long id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

//    public String encodePassword(String password, PasswordEncoder passwordEncoder){
//        return passwordEncoder.encode(password);
}// "단일 책임 원칙(Single Responsibility Principle, SRP)"과 "분리 및 추상화 지키기위해 User.java에서 Userservice로이동
//UserService 사용자의 데이터를 처리하고 관리하는 데 필요한 로직을 캡슐화 -> 여기가 적합.


/*
getauthentication부분 설명
* 인증 과정 후에는 성공적으로 검증된 사용자 정보와 권한 정보를 포함하여 새로운 UsernamePasswordAuthenticationToken 객체를 생성할 수 있습니다.
* 이때, 권한 정보(GrantedAuthority 목록)를 포함하여 생성자를 호출하면 내부적으로 authenticated 속성이 true로 설정됩니다.*/