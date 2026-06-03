package com.example.dailyquest.dto.response;

import com.example.dailyquest.model.Difficulty;
import com.example.dailyquest.model.TaskType;

import java.time.LocalDateTime;

public record DailyTaskResponse (
    Long id,
    String title,
    String description,
    Difficulty difficulty,
    TaskType taskType,
    Integer baseXp,
    Boolean active,
    LocalDateTime createdAt
) {

}
