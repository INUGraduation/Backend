package com.example.inu.domain.recruitments.repositories.recruitments;

import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentTechStackRepository extends JpaRepository<RecruitmentTechStack,Long> {
    List<RecruitmentTechStack> findByRecruitment(Recruitment recruitment);
}
