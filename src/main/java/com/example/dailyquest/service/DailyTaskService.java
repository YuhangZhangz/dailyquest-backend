package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.exception.DailyTaskNotFoundException;
import com.example.dailyquest.exception.TaskAlreadyCompletedException;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.DailyTask;
import com.example.dailyquest.model.TaskType;
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
        
        if (!task.getActive()) {
            throw new TaskAlreadyCompletedException(id);
        }
        
        if (request.taskType() == TaskType.TODO) {
            task.setDueDate(request.dueDate());
        } else {
            task.setDueDate(null);
        }

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
            request.difficulty(),
            request.taskType()
        );

        task.setUser(currentUser);
        
        if (request.taskType() == TaskType.TODO) {
            task.setDueDate(request.dueDate());
        }

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
        // Get the current user from the security context
        AppUser currentUser = getCurrentUser();
        
        // Find the task by ID and ensure it belongs to the current user
        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
        .orElseThrow(() -> new DailyTaskNotFoundException(id));
        
        // For Daily tasks, mark as completed and update streaks
        if (task.getTaskType() == TaskType.TODO && !task.getActive()) {
            throw new TaskAlreadyCompletedException(id);
        }

        // Calculate XP gain and update user's total XP and level
        int gainedXp = task.getBaseXp();
        currentUser.setTotalXp(currentUser.getTotalXp() + gainedXp);
        
        int newLevel = (currentUser.getTotalXp() / 100) + 1;
        currentUser.setLevel(newLevel);
        
        // Habit tasks can be completed repeatedly, so keep them active and track the count.
        if (task.getTaskType() == TaskType.HABIT) {
            task.setCompletedCount(task.getCompletedCount() + 1);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }
        
        // For Daily tasks, mark as completed and update streaks
        if(task.getTaskType() == TaskType.DAILY) {
            LocalDate today = LocalDate.now();
            if(task.getLastCompletedDate() != null &&
               task.getLastCompletedDate().isEqual(today)) {
                throw new TaskAlreadyCompletedException(id);
            }

            task.setLastCompletedDate(today);
            task.setCompletedCount(task.getCompletedCount() + 1);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }
        

        task.setActive(false);
        
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

    public DailyTaskResponse revertDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        if (task.getActive()) {
            return toResponse(task);
        }

        task.setActive(true);

        int lostXp = task.getBaseXp();

        currentUser.setTotalXp(
            Math.max(0, currentUser.getTotalXp() - lostXp)
        );

        int newLevel = (currentUser.getTotalXp() / 100) + 1;
        currentUser.setLevel(newLevel);

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
            task.getTaskType(),
            task.getBaseXp(),
            task.getActive(),
            task.getCreatedAt(),
            task.getCompletedCount(),
            task.getLastCompletedDate(),
            task.getDueDate()
        );
    }
}