package com.example.inu.domain.user.repositories;

import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.entities.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPositionRepository extends JpaRepository<UserPosition,Long> {
    List<UserPosition> findByUser(User user);
}