package com.example.dailyquest.controller;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.service.DailyTaskService;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/daily-tasks")
public class DailyTaskController {

    private final DailyTaskService dailyTaskService;

    public DailyTaskController(DailyTaskService dailyTaskService) {
        this.dailyTaskService = dailyTaskService;
    }

    @GetMapping
    public List<DailyTaskResponse> getAllDailyTasks() {
        return dailyTaskService.getAllDailyTasks();
    }
    
    @GetMapping("/{id}")
    public DailyTaskResponse getDailyTaskById(@PathVariable Long id) {
        return dailyTaskService.getDailyTaskById(id);
    }

    @PutMapping("/{id}")
    public DailyTaskResponse updateDailyTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateDailyTaskRequest request
    ) {
        // Implement update logic in the service layer
        return dailyTaskService.updateDailyTask(id, request);
    }

    @PostMapping
    public DailyTaskResponse createDailyTask(
            @Valid @RequestBody CreateDailyTaskRequest request
    ) {
        return dailyTaskService.createDailyTask(request);
    }

    @DeleteMapping("/{id}")
    public void deleteDailyTask(@PathVariable Long id) {
        dailyTaskService.deleteDailyTask(id);
    }

    @PatchMapping("/{id}/complete")
    public DailyTaskResponse completeDailyTask(@PathVariable Long id) {
        return dailyTaskService.completeDailyTask(id);
    }
}