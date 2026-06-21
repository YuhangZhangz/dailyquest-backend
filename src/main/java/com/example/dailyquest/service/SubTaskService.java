package com.example.dailyquest.service;

import org.springframework.stereotype.Service;

import com.example.dailyquest.dto.request.CreateSubTaskRequest;
import com.example.dailyquest.dto.response.SubTaskResponse;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.SubTask;
import com.example.dailyquest.model.Task;
import com.example.dailyquest.model.TaskType;
import com.example.dailyquest.repository.DailyTaskRepository;
import com.example.dailyquest.repository.SubTaskRepository;

import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SubTaskService {

    // Repository for subtask CRUD operations
    private final SubTaskRepository subTaskRepository;

    // Repository for finding parent tasks
    private final DailyTaskRepository dailyTaskRepository;

    public SubTaskService(
        SubTaskRepository subTaskRepository,
        DailyTaskRepository dailyTaskRepository
    ) {
        this.subTaskRepository = subTaskRepository;
        this.dailyTaskRepository = dailyTaskRepository;
    }

    /**
     * Create a subtask under an existing task.
     *
     * Rules:
     * - User must own the parent task.
     * - HABIT tasks do not support subtasks.
     */
    public SubTaskResponse createSubTask(Long taskId, CreateSubTaskRequest request) {

        // Get authenticated user
        AppUser currentUser = getCurrentUser();

        // Find parent task
        Task task = dailyTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify task ownership
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not allowed");
        }

        // Only DAILY and TODO support subtasks
        if (task.getTaskType() == TaskType.HABIT) {
            throw new RuntimeException("Habits do not support subtasks");
        }

        // Create new subtask
        SubTask subTask = new SubTask();
        subTask.setTitle(request.title());
        subTask.setCompleted(false);

        // Link subtask to parent task
        subTask.setDailyTask(task);

        // Persist to database
        SubTask saved = subTaskRepository.save(subTask);

        // Convert Entity -> DTO
        return mapToResponse(saved);
    }

    /**
     * Toggle completion status of a subtask.
     *
     * false -> true
     * true  -> false
     */
    public SubTaskResponse toggleSubTask(Long subTaskId) {

        AppUser currentUser = getCurrentUser();

        SubTask subTask = subTaskRepository.findById(subTaskId)
            .orElseThrow(() -> new RuntimeException("Subtask not found"));

        // Verify ownership through parent task
        if (!subTask.getDailyTask().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not allowed");
        }

        // Flip completion state
        subTask.setCompleted(!subTask.isCompleted());

        SubTask saved = subTaskRepository.save(subTask);

        return mapToResponse(saved);
    }

    /**
     * Delete a subtask.
     */
    public void deleteSubTask(Long subTaskId) {

        AppUser currentUser = getCurrentUser();

        SubTask subTask = subTaskRepository.findById(subTaskId)
            .orElseThrow(() -> new RuntimeException("Subtask not found"));

        // Verify ownership through parent task
        if (!subTask.getDailyTask().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not allowed");
        }

        subTaskRepository.delete(subTask);
    }

    /**
     * Convert SubTask entity into response DTO.
     *
     * Prevents exposing unnecessary fields such as:
     * - parent Task
     * - user information
     */
    private SubTaskResponse mapToResponse(SubTask subTask) {
        return new SubTaskResponse(
            subTask.getId(),
            subTask.getTitle(),
            subTask.isCompleted()
        );
    }

    /**
     * Retrieve currently authenticated user id from JWT context,
     * then load the user from database.
     */
    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}