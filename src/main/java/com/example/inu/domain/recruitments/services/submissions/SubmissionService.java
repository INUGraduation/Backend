package com.example.inu.domain.recruitments.services.submissions;


import com.example.inu.domain.recruitments.dtos.submissions.AnswerReadDto;
import com.example.inu.domain.recruitments.dtos.submissions.ChoiceReadDto;
import com.example.inu.domain.recruitments.dtos.submissions.SubmissionDetailDto;
import com.example.inu.domain.recruitments.dtos.submissions.SubmissionReadDto;
import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.entities.submissions.Answer;
import com.example.inu.domain.recruitments.entities.submissions.Choice;
import com.example.inu.domain.recruitments.entities.submissions.Submission;
import com.example.inu.domain.recruitments.repositories.applications.ApplicationRepository;
import com.example.inu.domain.recruitments.repositories.submissions.AnswerRepository;
import com.example.inu.domain.recruitments.repositories.submissions.ChoiceRepository;
import com.example.inu.domain.recruitments.repositories.submissions.SubmissionRepository;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private EmailService emailService;


    // 제출서 생성
    public Submission createSubmission(Long applicationId) {
        Submission submission = new Submission();

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("해당 신청서 양식은 존재하지 않습니다: " + applicationId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        submission.setApplication(application);
        submission.setUser(user);

        String ownerMail = application.getRecruitment().getUser().getEmail();
        emailService.sendSimpleEmail(ownerMail, "[Gaemoim] 회원님의 모집글에 지원한 사람이 있습니다.", "<p>[Gaemoim] <a href='http://gaemoim.site'>gaemoim.site</a></p>");

        return submissionRepository.save(submission);

    }

    // 제출서 전체 조회
    @Transactional(readOnly = true)
    public SubmissionReadDto getSubmissions(Long applicationId) {
        List<Submission> submissions = submissionRepository.findByApplicationId(applicationId);
        SubmissionReadDto dto = new SubmissionReadDto();

        dto.setSubmissionIds(submissions.stream()
                .map(Submission::getId)
                .collect(Collectors.toList()));

        dto.setProfiles(submissions.stream()
                .map(submission -> submission.getUser().getPhoto()).collect(Collectors.toList()));

        dto.setUserIds(submissions.stream()
                .map(submission -> submission.getUser().getId()).collect(Collectors.toList()));

        dto.setUserNames(submissions.stream()
                .map(submission -> submission.getUser().getName()).collect(Collectors.toList()));

        return dto;

    }

    /********** 제출서 상세 조회 start **********/
    @Transactional(readOnly = true)
    public Optional<SubmissionDetailDto> getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .map(this::convertToSubmissionDto);
    }

    // 제출서 인스턴스를 해당 dto로 변환
    private SubmissionDetailDto convertToSubmissionDto(Submission submission) {
        SubmissionDetailDto dto = new SubmissionDetailDto();
        dto.setUserId(submission.getUser().getId());
        dto.setStatus(submission.getStatus());

        List<Answer> answers = answerRepository.findBySubmissionId(submission.getId());
        dto.setAnswers(answers.stream()
                .map(this::convertToAnswerReadDto)
                .collect(Collectors.toList()));

        return dto;
    }

    // 답변 인스턴스를 해당 dto로 변환
    private AnswerReadDto convertToAnswerReadDto(Answer answer) {
        AnswerReadDto dto = new AnswerReadDto();
        dto.setQuestionId(answer.getQuestion().getId());

        if(answer.getQuestion().getType().equals("multiple")) {
            List<Choice> choices = choiceRepository.findByAnswer(answer);
            dto.setChoices(choices.stream()
                    .map(this::convertToChoiceReadDto)
                    .collect(Collectors.toList()));
            return dto;
        }
        else {
            dto.setContent(answer.getContent());
            return dto;
        }
    }

    // 선택 인스턴스를 해당 dto로 변환
    private ChoiceReadDto convertToChoiceReadDto(Choice choice) {
        ChoiceReadDto dto = new ChoiceReadDto();
        dto.setOptionId(choice.getOption().getId());

        return dto;
    }


    /********** 제출서 상세 조회 end **********/


    // 제출서 수락
    public void acceptSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 제출서입니다: " + submissionId));

        /**************
         추후 이 자리에 보안 로직 추가..
         **************/

        submission.setStatus("accepted");
        submissionRepository.save(submission);

        String email = submission.getUser().getEmail();
        emailService.sendSimpleEmail(email, "[Gaemoim] 회원님의 지원이 수락 되었습니다.", "<p>[Gaemoim] <a href='http://gaemoim.site'>gaemoim.site</a></p>");
    }

    // 제출서 거절
    public void rejectSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 제출서입니다: " + submissionId));

        /**************
         추후 이 자리에 보안 로직 추가..
         **************/

        submission.setStatus("rejected");
        submissionRepository.save(submission);

        String email = submission.getUser().getEmail();
        emailService.sendSimpleEmail(email, "[Gaemoim] 회원님의 지원이 거절 되었습니다.", "<p>[Gaemoim] <a href='http://gaemoim.site'>gaemoim.site</a></p>");
    }
}