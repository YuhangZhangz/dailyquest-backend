package com.example.dailyquest.dto.response;

public record RewardSummaryResponse(
    int availableCoins,
    long rewardsUnlocked
) {
}
