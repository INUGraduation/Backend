package com.example.inu.domain.recruitments.repositories.applications;

import com.example.inu.domain.recruitments.entities.applications.Option;
import com.example.inu.domain.recruitments.entities.applications.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,Long> {
    List<Option> findByQuestion(Question question);
}
