package com.example.inu.domain.recruitments.services.recruitments;

import com.example.inu.domain.common.entities.Position;
import com.example.inu.domain.common.entities.TechStack;
import com.example.inu.domain.common.repositories.PositionRepository;
import com.example.inu.domain.common.repositories.TechStackRepository;
import com.example.inu.domain.recruitments.dtos.recruitments.RecruitmentCreateDto;
import com.example.inu.domain.recruitments.dtos.recruitments.RecruitmentDetailDto;
import com.example.inu.domain.recruitments.dtos.recruitments.RecruitmentReadDto;
import com.example.inu.domain.recruitments.dtos.recruitments.RecruitmentUpdateDto;
import com.example.inu.domain.recruitments.dtos.submissions.SubmissionStatusDto;
import com.example.inu.domain.recruitments.entities.applications.Application;
import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentPosition;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentTechStack;
import com.example.inu.domain.recruitments.entities.submissions.Submission;
import com.example.inu.domain.recruitments.repositories.applications.ApplicationRepository;
import com.example.inu.domain.recruitments.repositories.recruitments.RecruitmentPositionRepository;
import com.example.inu.domain.recruitments.repositories.recruitments.RecruitmentRepository;
import com.example.inu.domain.recruitments.repositories.recruitments.RecruitmentTechStackRepository;
import com.example.inu.domain.recruitments.repositories.submissions.SubmissionRepository;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TechStackRepository techStackRepository;

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private RecruitmentTechStackRepository recruitmentTechStackRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // 모집글 생성 메소드
    @Transactional
    public Recruitment createRecruitment(RecruitmentCreateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        Recruitment recruitment = new Recruitment();
        recruitment.setType(dto.getType());
        recruitment.setTitle(dto.getTitle());
        recruitment.setNumber(dto.getNumber());
        recruitment.setStartDate(dto.getStartDate());
        recruitment.setEndDate(dto.getEndDate());
        recruitment.setDeadline(dto.getDeadline());
        recruitment.setUser(user);  // 사용자 연결
        recruitment.setIntroduction(dto.getIntroduction());

        recruitmentRepository.save(recruitment);

        // 모집글-기술분야 인스턴스들 생성
        List<Position> positions = positionRepository.findAllById(dto.getPositionIds());
        for(Position position : positions) {
            RecruitmentPosition recruitmentPosition = new RecruitmentPosition(null, recruitment, position);
            recruitmentPositionRepository.save(recruitmentPosition);
        }

        // 모집글-기술스택 인스턴스들 생성
        List<TechStack> techStacks =techStackRepository.findAllById(dto.getTechStackIds());
        for(TechStack techStack : techStacks) {
            RecruitmentTechStack recruitmentTechStack = new RecruitmentTechStack(null, recruitment, techStack);
            recruitmentTechStackRepository.save(recruitmentTechStack);
        }

        return recruitment;
    }

    // 모집글 전체 조회 메소드
//    public List<RecruitmentListDto> getAllRecruitments() {
//        return recruitmentRepository.findAll().stream().map(recruitment -> {
//            RecruitmentListDto dto = new RecruitmentListDto();
//            dto.setId(recruitment.getId());
//            dto.setTitle(recruitment.getTitle());
//            dto.setType(recruitment.getType());
//            dto.setNumber(recruitment.getNumber());
//            dto.setStartDate(recruitment.getStartDate());
//            dto.setEndDate(recruitment.getEndDate());
//            dto.setDeadline(recruitment.getDeadline());
//            dto.setClosing(recruitment.isClosing());
//            dto.setUserId(recruitment.getUser().getId());
//
//            // Position 이름 추출
//            List<String> positionNames = recruitmentPositionRepository.findByRecruitment(recruitment).stream()
//                    .map(recruitmentPosition -> recruitmentPosition.getPosition().getName())
//                    .collect(Collectors.toList());
//            dto.setPositions(positionNames);
//
//            // TechStack 이름 추출
//            List<String> techStackNames = recruitmentTechStackRepository.findByRecruitment(recruitment).stream()
//                    .map(recruitmentTechStack -> recruitmentTechStack.getTechStack().getName())
//                    .collect(Collectors.toList());
//            dto.setTechStacks(techStackNames);
//
//            return dto;
//        }).collect(Collectors.toList());
//    }


    // 모집글 상세 조회 메소드
    public Optional<RecruitmentDetailDto> getRecruitmentById(Long id) {
        return recruitmentRepository.findById(id).map(recruitment -> {
            RecruitmentDetailDto dto = new RecruitmentDetailDto();
            dto.setId(recruitment.getId());
            dto.setTitle(recruitment.getTitle());
            dto.setType(recruitment.getType());
            dto.setNumber(recruitment.getNumber());
            dto.setStartDate(recruitment.getStartDate());
            dto.setEndDate(recruitment.getEndDate());
            dto.setDeadline(recruitment.getDeadline());
            dto.setClosing(recruitment.isClosing());
            dto.setName(recruitment.getUser().getName());
            dto.setUserId(recruitment.getUser().getId());
            dto.setPhoto(recruitment.getUser().getPhoto());
            dto.setIntroduction(recruitment.getIntroduction());
            dto.setCreatedDate(recruitment.getCreatedAt());

            Application application = applicationRepository.findByRecruitmentId(recruitment.getId())
                    .orElseThrow(() -> new RuntimeException("이 모집글은 아직 신청서 양식이 없습니다."));
            dto.setApplicationId(application.getId());

            // Position 이름 추출
            List<String> positionNames = recruitmentPositionRepository.findByRecruitment(recruitment)
                    .stream()
                    .map(recruitmentPosition -> recruitmentPosition.getPosition().getName())
                    .collect(Collectors.toList());
            dto.setPositions(positionNames);

            // TechStack 이름 추출
            List<String> techStackNames = recruitmentTechStackRepository.findByRecruitment(recruitment)
                    .stream()
                    .map(recruitmentTechStack -> recruitmentTechStack.getTechStack().getName())
                    .collect(Collectors.toList());
            dto.setTechStacks(techStackNames);

            return dto;
        });
    }

    //모집글 필터링 조회 (type, positionId, techStackId)
    public List<RecruitmentReadDto> filterRecruitments(String type, Long positionId, Long techStackId) {
        Specification<Recruitment> spec = Specification.where(null);

        if(type!=null){
            spec= spec.and(RecruitmentSpecifications.hasType(type));
        }
        if(positionId!=null){
            spec= spec.and(RecruitmentSpecifications.hasPosition(positionId));
        }
        if(techStackId!=null){
            spec= spec.and(RecruitmentSpecifications.hasTechStack(techStackId));
        }
        return recruitmentRepository.findAll(spec).stream()
                .map(this::convertToRecruitmentReadDto)
                .collect(Collectors.toList());
    }

    private RecruitmentReadDto convertToRecruitmentReadDto(Recruitment recruitment) {
        RecruitmentReadDto dto = new RecruitmentReadDto();
        dto.setId(recruitment.getId());
        dto.setType(recruitment.getType());
        dto.setTitle(recruitment.getTitle());
        dto.setNumber(recruitment.getNumber());
        dto.setStartDate(recruitment.getStartDate());
        dto.setEndDate(recruitment.getEndDate());
        dto.setDeadline(recruitment.getDeadline());
        dto.setClosing(recruitment.isClosing());
        dto.setUserId(recruitment.getUser().getId());

        // Position 이름 추출
        List<String> positionNames = recruitmentPositionRepository.findByRecruitment(recruitment).stream()
                .map(recruitmentPosition -> recruitmentPosition.getPosition().getName())
                .collect(Collectors.toList());
        dto.setPositions(positionNames);

        // TechStack 이름 추출
        List<String> techStackNames = recruitmentTechStackRepository.findByRecruitment(recruitment).stream()
                .map(recruitmentTechStack -> recruitmentTechStack.getTechStack().getName())
                .collect(Collectors.toList());
        dto.setTechStacks(techStackNames);

        return dto;

    }

    // 내가 쓴 모집글 조회
    public List<RecruitmentReadDto> filterMyRecruitments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        return recruitmentRepository.findByUser(user).stream().map(recruitment -> {
            RecruitmentReadDto dto = new RecruitmentReadDto();
            dto.setId(recruitment.getId());
            dto.setTitle(recruitment.getTitle());
            dto.setType(recruitment.getType());
            dto.setNumber(recruitment.getNumber());
            dto.setStartDate(recruitment.getStartDate());
            dto.setEndDate(recruitment.getEndDate());
            dto.setDeadline(recruitment.getDeadline());
            dto.setClosing(recruitment.isClosing());
            dto.setUserId(recruitment.getUser().getId());

            // Position 이름 추출
            List<String> positionNames = recruitmentPositionRepository.findByRecruitment(recruitment).stream()
                    .map(recruitmentPosition -> recruitmentPosition.getPosition().getName())
                    .collect(Collectors.toList());
            dto.setPositions(positionNames);

            // TechStack 이름 추출
            List<String> techStackNames = recruitmentTechStackRepository.findByRecruitment(recruitment).stream()
                    .map(recruitmentTechStack -> recruitmentTechStack.getTechStack().getName())
                    .collect(Collectors.toList());
            dto.setTechStacks(techStackNames);

            return dto;
        }).collect(Collectors.toList());
    }

    // 내가 신청한 모집글 조회
    public List<SubmissionStatusDto> filterAppliedRecruitments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        List<Submission> submissions = submissionRepository.findByUser(user);
        List<SubmissionStatusDto> dtos = new ArrayList<>();

        for (Submission s : submissions) {
            Recruitment recruitment = s.getApplication().getRecruitment();
            SubmissionStatusDto dto = new SubmissionStatusDto();
            dto.setId(recruitment.getId());
            dto.setType(recruitment.getType());
            dto.setTitle(recruitment.getTitle());
            dto.setNumber(recruitment.getNumber());
            dto.setStartDate(recruitment.getStartDate());
            dto.setEndDate(recruitment.getEndDate());
            dto.setDeadline(recruitment.getDeadline());
            dto.setStatus(s.getStatus());

            // Position 이름 추출
            List<String> positionNames = recruitmentPositionRepository.findByRecruitment(recruitment).stream()
                    .map(recruitmentPosition -> recruitmentPosition.getPosition().getName())
                    .collect(Collectors.toList());
            dto.setPositions(positionNames);

            // TechStack 이름 추출
            List<String> techStackNames = recruitmentTechStackRepository.findByRecruitment(recruitment).stream()
                    .map(recruitmentTechStack -> recruitmentTechStack.getTechStack().getName())
                    .collect(Collectors.toList());
            dto.setTechStacks(techStackNames);

            dtos.add(dto);
        }

        return dtos;
    }
    // 모집글 수정 메소드
    public Recruitment updateRecruitment(Long recruitmentId, RecruitmentUpdateDto dto) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집글입니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        if(!recruitment.getUser().equals(user)) {
            throw new IllegalStateException("수정 권한이 없는 모집글입니다.");
        }

        if(dto.getType() != null) {
            recruitment.setType(dto.getType());
        }
        if(dto.getTitle() != null) {
            recruitment.setTitle(dto.getTitle());
        }
        if(dto.getNumber() != null) {
            recruitment.setNumber(dto.getNumber());
        }
        if(dto.getStartDate() != null) {
            recruitment.setStartDate(dto.getStartDate());
        }
        if(dto.getEndDate() != null) {
            recruitment.setEndDate(dto.getEndDate());
        }
        if(dto.getDeadline() != null) {
            recruitment.setDeadline(dto.getDeadline());
        }
        if(dto.getClosing() != null) {
            recruitment.setClosing(dto.getClosing());
        }

        if(dto.getPositionIds() != null) {
            updateRecruitmentPositions(recruitment, dto.getPositionIds());
        }

        if(dto.getTechStackIds() != null) {
            updateRecruitmentTechStacks(recruitment, dto.getTechStackIds());
        }

        return recruitmentRepository.save(recruitment);
    }

    // 모집글 포지션 수정 메소드
    private void updateRecruitmentPositions(Recruitment recruitment, List<Long> newPositionIds) {
        List<RecruitmentPosition> existingPositions = recruitmentPositionRepository.findByRecruitment(recruitment);
        Set<Long> existingPositionIds = existingPositions.stream()
                .map(rp -> rp.getPosition().getId())
                .collect(Collectors.toSet());

        // 원래 존재하던 포지션이 새로운 포지션 목록엔 없으면 삭제
        Set<Long> newPositionIdSet = new HashSet<>(newPositionIds);
        existingPositions.forEach(rp -> {
            if(!newPositionIdSet.contains(rp.getPosition().getId())) {
                recruitmentPositionRepository.delete(rp);
            }
        });

        // 원래 존재하던 포지션에 없던 새로운 포지션 목록이면 추가
        newPositionIdSet.forEach(id -> {
            if(!existingPositionIds.contains(id)) {
                Position position = positionRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 Position의 아이디입니다: " + id));
                recruitmentPositionRepository.save(new RecruitmentPosition(null, recruitment, position));
            }
        });
    }

    // 모집글 기술스택 수정 메소드
    private void updateRecruitmentTechStacks(Recruitment recruitment, List<Long> newTechStackIds) {
        List<RecruitmentTechStack> existingTechStacks = recruitmentTechStackRepository.findByRecruitment(recruitment);
        Set<Long> existingTechStackIds = existingTechStacks.stream()
                .map(rt -> rt.getTechStack().getId())
                .collect(Collectors.toSet());

        Set<Long> newTechStackIdSet = new HashSet<>(newTechStackIds);
        // 원래 존재하던 기술스택이 새로운 기술스택 목록엔 없으면 삭제
        existingTechStacks.forEach(rt -> {
            if (!newTechStackIdSet.contains(rt.getTechStack().getId())) {
                recruitmentTechStackRepository.delete(rt);
            }
        });

        // 원래 존재하던 기술스택에 없던 새로운 기술스택 목록이면 추가
        newTechStackIdSet.forEach(id -> {
            if (!existingTechStackIds.contains(id)) {
                TechStack techStack = techStackRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 TechStack의 아이디입니다: " + id));
                recruitmentTechStackRepository.save(new RecruitmentTechStack(null, recruitment, techStack));
            }
        });
    }

    // 모집글 삭제 메소드
    @Transactional
    public void deleteRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모집글입니다: " + recruitmentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        if(!recruitment.getUser().equals(user)) {
            throw new IllegalStateException("삭제 권한이 없는 모집글입니다.");
        }
        recruitmentRepository.delete(recruitment);
    }

    // 모집글 마감 메소드
    public void closeRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모집글입니다: " + recruitmentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 유저의 이메일 추출
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail)); // 유저의 이메일을 기준으로 User 인스턴스 추출

        if(!recruitment.getUser().equals(user)) {
            throw new IllegalStateException("마감 권한이 없는 모집글입니다.");
        }

        recruitment.setClosing(true);
        recruitmentRepository.save(recruitment);
    }


}