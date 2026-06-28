package com.example.dailyquest.repository;

import com.example.dailyquest.model.RewardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, Long> {
    Long countByUserId(Long userId);
}
