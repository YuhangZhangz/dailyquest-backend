package com.example.dailyquest.dto.response;

import com.example.dailyquest.model.Difficulty;
import java.time.LocalDateTime;

public record DailyTaskResponse (
    Long id,
    String title,
    String description,
    Difficulty difficulty,
    Integer baseXp,
    Boolean active,
    LocalDateTime createdAt
) {

}
