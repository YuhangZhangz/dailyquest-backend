package com.example.dailyquest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSubTaskRequest(
    @NotBlank(message = "Title is required")
    String title
) {}
