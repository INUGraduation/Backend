package com.example.inu.domain.recruitments.services.applications;


import com.example.inu.domain.recruitments.dtos.applications.QuestionCreateDto;
import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.entities.applications.Option;
import com.example.inu.domain.recruitments.entities.applications.Question;
import com.example.inu.domain.recruitments.repositories.applications.ApplicationRepository;
import com.example.inu.domain.recruitments.repositories.applications.OptionRepository;
import com.example.inu.domain.recruitments.repositories.applications.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;


    public Question createQuestion(QuestionCreateDto dto, Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 신청서입니다."));

        /************************************
         추후 이 자리에 보안 로직 필요
         ************************************/

        Question question = new Question();
        question.setType(dto.getType());
        question.setTitle(dto.getTitle());
        question.setApplication(application);

        questionRepository.save(question);

        // 객관식 유형이라면 option들 생성
        if("multiple".equals(dto.getType())) {
            for(String optionContent : dto.getOptions()) {
                Option option = new Option();
                option.setQuestion(question);
                option.setContent(optionContent);

                optionRepository.save(option);
            }
        }

        return question;
    }


}