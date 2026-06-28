package com.example.dailyquest.dto.response;

public record RedeemRewardResponse(
    Long rewardId,
    int costPaid,
    int remainingBalance
) {
}
