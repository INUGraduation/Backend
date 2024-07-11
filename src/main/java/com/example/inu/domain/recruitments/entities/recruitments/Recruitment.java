package com.example.inu.domain.recruitments.entities.recruitments;


import com.example.inu.domain.common.entities.BaseEntity;
import com.example.inu.domain.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recruitments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //스터디 or 프로젝트
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int number;

    // TemporalType.DATE로 설정함으로써 날짜만 저장할 것이라는 것을 명시(자바의 Date 타입은 시간까지 포함됨)
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date deadline;

    @Column(nullable = false)
    private boolean closing = false;

    @Column(nullable = true)
    private String introduction;


}