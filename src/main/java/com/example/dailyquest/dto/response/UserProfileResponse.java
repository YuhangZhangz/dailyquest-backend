package com.example.dailyquest.dto.response;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Integer totalXp,
        Integer level,
        Integer dailyStreak,
        int coinBalance
) {
}