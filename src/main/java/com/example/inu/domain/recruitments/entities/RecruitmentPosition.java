package com.example.inu.domain.recruitments.entities;


import com.example.inu.domain.common.entities.Position;
import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recruitment_positioins")
public class RecruitmentPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}
//Recruitment --- Position 중간테이블
