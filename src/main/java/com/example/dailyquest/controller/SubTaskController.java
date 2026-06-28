package com.example.dailyquest.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.example.dailyquest.dto.request.CreateSubTaskRequest;
import com.example.dailyquest.dto.request.UpdateSubTaskRequest;
import com.example.dailyquest.dto.response.SubTaskResponse;
import com.example.dailyquest.service.SubTaskService;

import java.util.List;

@RestController
@RequestMapping("/subtasks")
public class SubTaskController {
    // Service layer that contains subtask business logic
    private final SubTaskService subTaskService;

    public SubTaskController(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
    }

    /**
     * Create a new subtask under a parent task
     * 
     * Example:
     * POST /subtasks/task/1
     * 
     * Body:
     * {
     *  "title" : "test"
     * }
     */
    @PostMapping("/task/{taskId}")
    public SubTaskResponse createSubTask(
        @PathVariable Long taskId,
        @RequestBody CreateSubTaskRequest request
    ) {
        return subTaskService.createSubTask(taskId, request);
    }

    @PatchMapping("/task/{taskId}/sort-order")
    public List<SubTaskResponse> updateSortOrder(
            @PathVariable Long taskId,
            @RequestBody List<Long> subTaskIds
    ) {
        return subTaskService.updateSortOrder(taskId, subTaskIds);
    }

    /**
     * Toggle subtask completion status.
     * 
     * false <-> true
     * 
     * Example:
     * PATCH /subtasks/10/toggle
     */
    @PatchMapping("/{subTaskId}")
    public SubTaskResponse updateSubTask(
        @PathVariable Long subTaskId,
        @Valid @RequestBody UpdateSubTaskRequest request
    ) {
        return subTaskService.updateSubTask(subTaskId, request);
    }

    @PatchMapping("/{subTaskId}/toggle")
    public SubTaskResponse toggleSubTask(@PathVariable Long subTaskId) {
        return subTaskService.toggleSubTask(subTaskId);
    }

    /**
     * Delete a subtask.
     * 
     * Example:
     * DELETE /subtasks/10
     */
    @DeleteMapping("/{subTaskId}")
    public void deleteSubTask(@PathVariable Long subTaskId) {
        subTaskService.deleteSubTask(subTaskId);
    }
}
