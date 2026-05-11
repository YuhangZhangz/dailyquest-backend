package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.exception.DailyTaskNotFoundException;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.DailyTask;
import com.example.dailyquest.repository.AppUserRepository;
import com.example.dailyquest.repository.DailyTaskRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailyTaskService {

    private final DailyTaskRepository dailyTaskRepository;
    private final AppUserRepository appUserRepository;

    public DailyTaskService(DailyTaskRepository dailyTaskRepository,
                            AppUserRepository appUserRepository
    ) {
        this.dailyTaskRepository = dailyTaskRepository;
        this.appUserRepository = appUserRepository;
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

    public DailyTaskResponse completeDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        task.setActive(false);

        int gainedXp = task.getBaseXp();
        currentUser.setTotalXp(currentUser.getTotalXp() + gainedXp);

        int newLevel = (currentUser.getTotalXp() / 100) + 1;
        currentUser.setLevel(newLevel);

        LocalDate today = LocalDate.now();
        LocalDate lastCompletedDate = currentUser.getLastCompletedDate();

        if (lastCompletedDate == null) {
            currentUser.setDailyStreak(1);
        } else if (lastCompletedDate.isEqual(today)) {
            // Same day completion, do not update streak
        } else if (lastCompletedDate.plusDays(1).isEqual(today)) {
            currentUser.setDailyStreak(currentUser.getDailyStreak() + 1);
        } else {
            currentUser.setDailyStreak(1);
        }

        currentUser.setLastCompletedDate(today);

        appUserRepository.save(currentUser);
        DailyTask updatedTask = dailyTaskRepository.save(task);

        return toResponse(updatedTask);
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