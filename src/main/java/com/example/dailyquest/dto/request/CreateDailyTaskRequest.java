package com.example.dailyquest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.dailyquest.model.Difficulty;

public record CreateDailyTaskRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Difficulty is required")
    Difficulty difficulty
) {}