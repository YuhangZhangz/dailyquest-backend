package com.example.dailyquest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.dailyquest.model.Difficulty;
import com.example.dailyquest.model.GrowthCategory;
import com.example.dailyquest.model.TaskType;
import java.time.LocalDate;

public record CreateDailyTaskRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Difficulty is required")
    Difficulty difficulty,

    @NotNull(message = "Task type is required")
    TaskType taskType,

    GrowthCategory growthCategory,

    LocalDate dueDate

) {
    public CreateDailyTaskRequest {
        if (growthCategory == null) {
            growthCategory = GrowthCategory.NONE;
        }
    }
}
