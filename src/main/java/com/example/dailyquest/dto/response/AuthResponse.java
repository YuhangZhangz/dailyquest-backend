package com.example.dailyquest.dto.response;

public record AuthResponse(
    Long userId,
    String username,
    String email,
    String token
) {
}