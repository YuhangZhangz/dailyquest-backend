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
                            AppUserRepository appUserRepository) {
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
        } else {
            task.setDueDate(null);
        }

        DailyTask savedTask = dailyTaskRepository.save(task);

        return toResponse(savedTask);
    }

    public DailyTaskResponse updateDailyTask(Long id, CreateDailyTaskRequest request) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        if (!task.getActive() && task.getTaskType() == TaskType.TODO) {
            throw new TaskAlreadyCompletedException(id);
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDifficulty(request.difficulty());
        task.setBaseXp(request.difficulty().getBaseXp());
        task.setTaskType(request.taskType());

        if (request.taskType() == TaskType.TODO) {
            task.setDueDate(request.dueDate());
        } else {
            task.setDueDate(null);
        }

        DailyTask updatedTask = dailyTaskRepository.save(task);

        return toResponse(updatedTask);
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

        int gainedXp = task.getBaseXp();

        if (task.getTaskType() == TaskType.HABIT) {
            int currentCount = getSafeCompletedCount(task);
            task.setCompletedCount(currentCount + 1);

            addXp(currentUser, gainedXp);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.DAILY) {
            LocalDate today = LocalDate.now();

            if (task.getLastCompletedDate() != null &&
                    task.getLastCompletedDate().isEqual(today)) {
                throw new TaskAlreadyCompletedException(id);
            }

            int currentCount = getSafeCompletedCount(task);
            task.setCompletedCount(currentCount + 1);
            task.setLastCompletedDate(today);

            addXp(currentUser, gainedXp);
            updateUserStreak(currentUser, today);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.TODO) {
            if (!task.getActive()) {
                throw new TaskAlreadyCompletedException(id);
            }

            task.setActive(false);

            addXp(currentUser, gainedXp);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        return toResponse(task);
    }

    public DailyTaskResponse revertDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        DailyTask task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        int lostXp = task.getBaseXp();

        if (task.getTaskType() == TaskType.HABIT) {
            int currentCount = getSafeCompletedCount(task);

            if (currentCount <= 0) {
                return toResponse(task);
            }

            task.setCompletedCount(currentCount - 1);
            removeXp(currentUser, lostXp);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.DAILY) {
            LocalDate today = LocalDate.now();

            if (task.getLastCompletedDate() == null ||
                    !task.getLastCompletedDate().isEqual(today)) {
                return toResponse(task);
            }

            int currentCount = getSafeCompletedCount(task);

            if (currentCount > 0) {
                task.setCompletedCount(currentCount - 1);
            }

            task.setLastCompletedDate(null);
            removeXp(currentUser, lostXp);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.TODO) {
            if (task.getActive()) {
                return toResponse(task);
            }

            task.setActive(true);
            removeXp(currentUser, lostXp);

            appUserRepository.save(currentUser);
            DailyTask updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        return toResponse(task);
    }

    private int getSafeCompletedCount(DailyTask task) {
        return task.getCompletedCount() == null ? 0 : task.getCompletedCount();
    }

    private void addXp(AppUser user, int xp) {
        user.setTotalXp(user.getTotalXp() + xp);
        user.setLevel((user.getTotalXp() / 100) + 1);
    }

    private void removeXp(AppUser user, int xp) {
        user.setTotalXp(Math.max(0, user.getTotalXp() - xp));
        user.setLevel((user.getTotalXp() / 100) + 1);
    }

    private void updateUserStreak(AppUser currentUser, LocalDate today) {
        LocalDate lastCompletedDate = currentUser.getLastCompletedDate();

        if (lastCompletedDate == null) {
            currentUser.setDailyStreak(1);
        } else if (lastCompletedDate.isEqual(today)) {
            return;
        } else if (lastCompletedDate.plusDays(1).isEqual(today)) {
            currentUser.setDailyStreak(currentUser.getDailyStreak() + 1);
        } else {
            currentUser.setDailyStreak(1);
        }

        currentUser.setLastCompletedDate(today);
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