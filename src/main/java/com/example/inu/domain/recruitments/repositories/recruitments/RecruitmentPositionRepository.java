package com.example.inu.domain.recruitments.repositories.recruitments;

import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentPositionRepository extends JpaRepository<RecruitmentPosition, Long> {
    List<RecruitmentPosition> findByRecruitment(Recruitment recruitment);
}
