package com.example.inu.domain.recruitments.repositories.submissions;

import com.example.inu.domain.recruitments.entities.submissions.Submission;
import com.example.inu.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
    List<Submission> findByApplicationId(Long applicationId);
    List<Submission> findByUser(User user);
}
