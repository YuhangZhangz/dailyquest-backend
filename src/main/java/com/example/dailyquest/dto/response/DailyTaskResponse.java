package com.example.dailyquest.dto.response;

import com.example.dailyquest.model.Difficulty;
import com.example.dailyquest.model.TaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

public record DailyTaskResponse (
    Long id,
    String title,
    String description,
    Difficulty difficulty,
    TaskType taskType,
    Integer baseXp,
    Boolean active,
    LocalDateTime createdAt,
    Integer completedCount,
    LocalDate lastCompletedDate,
    LocalDate dueDate,
    Integer sortOrder,
    List<SubTaskResponse> subTasks
) {

}
