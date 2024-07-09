package com.example.inu.domain.common.repositories;


import com.example.inu.domain.common.entities.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    @Query("SELECT t.id FROM TechStack t WHERE t.name IN :names")
    List<Long> findAllIdsByName(@Param("names") List<String> names);
}