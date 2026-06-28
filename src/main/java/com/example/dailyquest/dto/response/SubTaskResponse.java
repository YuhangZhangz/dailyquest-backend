package com.example.dailyquest.dto.response;

public record SubTaskResponse(
    Long id,
    String title,
    Boolean completed,
    Integer sortOrder
) {}

