package com.example.inu.domain.recruitments.repositories.submissions;

import com.example.inu.domain.recruitments.entities.submissions.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    List<Answer> findBySubmissionId(Long sumissionId);
}
