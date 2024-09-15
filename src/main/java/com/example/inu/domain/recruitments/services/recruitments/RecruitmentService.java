package com.example.inu.domain.recruitments.services.recruitments;

import com.example.inu.domain.recruitments.repositories.recruitments.RecruitmentRepository;
import com.example.inu.domain.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private UserRepository userRepository;
}
