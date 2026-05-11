package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.exception.DailyTaskNotFoundException;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.DailyTask;
import com.example.dailyquest.repository.DailyTaskRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTaskService {

    private final DailyTaskRepository dailyTaskRepository;

    public DailyTaskService(DailyTaskRepository dailyTaskRepository) {
        this.dailyTaskRepository = dailyTaskRepository;
    }

    public List<DailyTaskResponse> getAllDailyTasks() {
        AppUser currentUser = getCurrentUser();

        return dailyTaskRepository.findByUserId(currentUser.getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public DailyTaskResponse getDailyTaskById(Long id) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new DailyTaskNotFoundException(id));

        return toResponse(task);
    }

    public DailyTaskResponse updateDailyTask(Long id, CreateDailyTaskRequest request) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new DailyTaskNotFoundException(id));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDifficulty(request.difficulty());
        task.setBaseXp(request.difficulty().getBaseXp());

        DailyTask updatedTask = dailyTaskRepository.save(task);

        return toResponse(updatedTask);
    }

    public DailyTaskResponse createDailyTask(CreateDailyTaskRequest request) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = new DailyTask(
            request.title(),
            request.description(),
            request.difficulty()
        );

        task.setUser(currentUser);

        DailyTask savedTask = dailyTaskRepository.save(task);

        return toResponse(savedTask);
    }

    public void deleteDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new DailyTaskNotFoundException(id));

        dailyTaskRepository.delete(task);
    }

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private DailyTaskResponse toResponse(DailyTask task) {
        return new DailyTaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getDifficulty(),
            task.getBaseXp(),
            task.getActive(),
            task.getCreatedAt()
        );
    }
}