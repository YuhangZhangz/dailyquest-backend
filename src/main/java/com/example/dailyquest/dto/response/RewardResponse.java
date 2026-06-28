package com.example.dailyquest.dto.response;

import java.time.LocalDateTime;

public record RewardResponse(
    Long id,
    String title,
    String description,
    int cost,
    String iconKey,
    boolean active,
    LocalDateTime createdAt
) {
}
