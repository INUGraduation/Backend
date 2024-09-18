package com.example.inu.domain.recruitments.services.applications;


import com.example.inu.domain.recruitments.dtos.applications.ApplicationReadDto;
import com.example.inu.domain.recruitments.dtos.applications.OptionReadDto;
import com.example.inu.domain.recruitments.dtos.applications.QuestionReadDto;
import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.entities.applications.Option;
import com.example.inu.domain.recruitments.entities.applications.Question;
import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.recruitments.repositories.applications.ApplicationRepository;
import com.example.inu.domain.recruitments.repositories.applications.OptionRepository;
import com.example.inu.domain.recruitments.repositories.applications.QuestionRepository;
import com.example.inu.domain.recruitments.repositories.recruitments.RecruitmentRepository;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;

    public Application createApplication(Long recruitmentId) {
        Application application = new Application();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("해당 모집글은 존재하지 않습니다: " + recruitmentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        if(!recruitment.getUser().equals(user)) {
            throw new IllegalStateException("신청서 생성 권한이 없는 모집글입니다.");
        }

        application.setRecruitment(recruitment);
        application = applicationRepository.save(application);
        return application;
    }

    /********** 신청서 조회 start **********/
    // 신청서 조회
    @Transactional(readOnly = true)
    public Optional<ApplicationReadDto> getApplication(Long recruitmentId) {
        return applicationRepository.findByRecruitmentId(recruitmentId)
                .map(this::convertToApplicatoinReadDto);
    }

    // 신청서 인스턴스를 해당 dto로 변환
    private ApplicationReadDto convertToApplicatoinReadDto(Application application) {
        ApplicationReadDto dto = new ApplicationReadDto();
        dto.setApplicationId(application.getId());

        List<Question> questions = questionRepository.findByApplication(application);
        dto.setQuestions(questions.stream()
                .map(this::convertToQuestionReadDto)
                .collect(Collectors.toList()));
        return dto;
    }

    // 질문 인스턴스를 해당 dto로 변환
    private QuestionReadDto convertToQuestionReadDto(Question question) {
        QuestionReadDto dto = new QuestionReadDto();
        dto.setQuestionId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setType(question.getType());

        if(question.getType().equals("multiple")) {
            List<Option> options = optionRepository.findByQuestion(question);
            dto.setOptions(options.stream()
                    .map(this::convertToOptionReadDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // 선택지 인스턴스를 해당 dto로 변환
    private OptionReadDto convertToOptionReadDto(Option option) {
        OptionReadDto dto = new OptionReadDto();
        dto.setOptionId(option.getId());
        dto.setContent(option.getContent());
        return dto;
    }



    /********** 신청서 조회 end **********/



}