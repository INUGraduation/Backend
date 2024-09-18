package com.example.inu.domain.recruitments.repositories.submissions;

import com.example.inu.domain.recruitments.entities.submissions.Answer;
import com.example.inu.domain.recruitments.entities.submissions.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice,Long> {
    List<Choice> findByAnswer(Answer answer);
}
