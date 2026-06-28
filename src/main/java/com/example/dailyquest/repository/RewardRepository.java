package com.example.dailyquest.repository;

import com.example.dailyquest.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserIdAndActiveTrue(Long userId);
    Optional<Reward> findByIdAndUserId(Long id, Long userId);
}
