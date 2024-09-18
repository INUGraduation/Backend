package com.example.inu.domain.recruitments.repositories.applications;

import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.entities.applications.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByApplication(Application application);
}
