package com.example.inu.domain.recruitments.services.submissions;

import com.example.inu.domain.recruitments.dtos.submissions.AnswerCreateDto;
import com.example.inu.domain.recruitments.entities.applications.Option;
import com.example.inu.domain.recruitments.entities.applications.Question;
import com.example.inu.domain.recruitments.entities.submissions.Answer;
import com.example.inu.domain.recruitments.entities.submissions.Choice;
import com.example.inu.domain.recruitments.entities.submissions.Submission;
import com.example.inu.domain.recruitments.repositories.applications.OptionRepository;
import com.example.inu.domain.recruitments.repositories.applications.QuestionRepository;
import com.example.inu.domain.recruitments.repositories.submissions.AnswerRepository;
import com.example.inu.domain.recruitments.repositories.submissions.ChoiceRepository;
import com.example.inu.domain.recruitments.repositories.submissions.SubmissionRepository;
import com.example.inu.global.s3.S3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private OptionRepository optionRepository;
//    @Autowired
//    private S3FileService s3FileService;
    //submission : 제출서
    public Answer createAnswer(AnswerCreateDto dto, Long questionId, Long submissionId, MultipartFile file) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 질문입니다."));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 제출서입니다."));

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setSubmission(submission);

        if("descriptive".equals(question.getType())){
            if(dto.getContent()!=null){
                answer.setContent(dto.getContent());
            }else{
                throw new RuntimeException("서술형 답변 내용이 없습니다.");
            }

        }else if("file".equals(question.getType())){
            if(file!=null){
                try {
                    String fileUrl= s3FileService.upload(file);
                    answer.setContent(fileUrl);
                }catch (S3Exception e){
                    throw new RuntimeException("파일이 업로드 중 오류가 발생했습니다");
                }
            }else {
                throw new RuntimeException("파일이 없습니다.");
            }
        } else if ("multiple".equals(question.getType())) {
            answerRepository.save(answer);
            List<Long> optionIds = dto.getOptionIds();
            if(optionIds != null){
                for (Long optionId : optionIds){
                    Option option = optionRepository.findById(optionId)
                            .orElseThrow(()-> new RuntimeException("존재하지 않은 선택지입니다."));
                    Choice choice = new Choice();
                    choice.setOption(option);
                    choice.setAnswer(answer);
                    choiceRepository.save(choice);
                }
            }
            return answer;

        }else {
            throw new RuntimeException("알 수 없는 질문 타입입니다.");
        }

        answerRepository.save(answer);
        return answer;
    }
}