package com.example.inu.domain.common.repositories;

import com.example.inu.domain.common.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    @Query("SELECT p.id FROM Position p WHERE p.name IN :names")
    List<Long> findAllIdsByName(@Param("names") List<String> names);
}