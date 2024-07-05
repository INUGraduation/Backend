package com.example.inu.domain.user.entities;


import com.example.inu.domain.common.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

//@NoArgsConstructor : 파라미터가 없는 디폴트 생성자를 생성
//@AllArgsConstructor : 모든 필드 값을 파라미터로 받는 생성자를 생성
//@RequiredArgsConstructor : final이나 @NonNull으로 선언된 필드만을 파라미터로 받는 생성자를 생성

@Entity
@Table(name = "users")// @Document로 MongoDB 컬렉션과 매핑됨.
@Builder//사용자 정의 생성자가 있으면 안됨.
@NoArgsConstructor // 기본 생성자를 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자를 생성
@Setter
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;


    private String photo;
    private String gender;
    private String intro;
    private String residence;
    private String status;
    private String github;
}