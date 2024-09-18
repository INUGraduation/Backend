package com.example.inu.domain.recruitments.services.recruitments;


import com.example.inu.domain.common.entities.Position;
import com.example.inu.domain.common.entities.TechStack;
import com.example.inu.domain.recruitments.entities.recruitments.Recruitment;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentPosition;
import com.example.inu.domain.recruitments.entities.recruitments.RecruitmentTechStack;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RecruitmentSpecifications {

    public static Specification<Recruitment> hasType(String type){
        return(root, query,cb) -> {
            if(type == null)
                return null;
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Recruitment> hasPosition(Long positionId) {
        return (root, query, cb) -> {
            if (positionId == null)
                return null;
            // Recruitment 엔티티에서 positions 필드를 조인합니다.
            Join<Recruitment, RecruitmentPosition> positionsJoin = root.join("positions");
            // 조인된 RecruitmentPosition에서 Position 엔티티를 다시 조인합니다.
            Join<RecruitmentPosition, Position> positionJoin = positionsJoin.join("position");
            // 조인된 Position의 id 필드와 매개변수로 제공된 positionId를 비교합니다.
            return cb.equal(positionJoin.get("id"), positionId);
        };
    }


    public static Specification<Recruitment> hasTechStack(Long techStackId) {
        return (root, query, cb) -> {
            if (techStackId == null)
                return null;
            // Recruitment 엔티티에서 techStacks 필드를 조인합니다.
            Join<Recruitment, RecruitmentTechStack> techStacksJoin = root.join("techStacks");
            // 조인된 RecruitmentTechStack에서 TechStack 엔티티를 다시 조인합니다.
            Join<RecruitmentTechStack, TechStack> techStackJoin = techStacksJoin.join("techStack");
            // 조인된 TechStack의 id 필드와 매개변수로 제공된 techStackId를 비교합니다.
            return cb.equal(techStackJoin.get("id"), techStackId);
        };
    }
}