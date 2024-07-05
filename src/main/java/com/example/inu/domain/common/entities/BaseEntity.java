package com.example.inu.domain.common.entities;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@MappedSuperclass// BaseEintity륾 굳이 테이블로 관리할 필요가 없으므로 테이블로 안생기는데  상속받은 애들한테는 테이블로 들어감엔티티등록
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;

    private Date deletedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
//BaseEntity 사용 이유 : 공통 필드인 createdAt, updatedAt, deletedAt를 BaseEntity 클래스로 분리하여 재사용할 수 있다.
// 이렇게 하면 다른 도메인 모델에서도 이러한 공통 속성을 쉽게 상속받아 사용할 수 있다.