package com.example.inu.domain.recruitments.repositories.recruitments;

import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment,Long> , JpaSpecificationExecutor<Recruitment> {
    List<Recruitment> findByUser(User user);
}
