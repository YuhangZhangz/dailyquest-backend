package com.example.dailyquest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateRewardRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    @Positive(message = "Cost must be positive")
    int cost,

    @NotBlank(message = "Icon key is required")
    String iconKey
) {
}
