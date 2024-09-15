package com.example.inu.domain.recruitments.repositories.recruitments;

import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment,Long> , JpaSpecificationExecutor<Recruitment> {
    List<Recruitment> findByUser(User user);
}

//Jpa는 인터페이스만 선언하고 따로 구현체는 없는 이유는??
//Spring Data JPA에서는 **인터페이스로 리포지토리(Repository)**를 정의하지만, 직접 구현체를 작성할 필요가 없는 이유는 Spring Data JPA가 프록시 객체를 생성하여 해당 인터페이스의 구현체를 자동으로 생성해주기 때문입니다.
// 이는 Spring의 **의존성 주입(Dependency Injection)**과 Spring Data JPA의 자동 구현 기능 덕분에 가능한 것입니다.