package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.dto.response.SubTaskResponse;
import com.example.dailyquest.exception.DailyTaskNotFoundException;
import com.example.dailyquest.exception.TaskAlreadyCompletedException;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.Task;
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

        return dailyTaskRepository
                .findByUserIdOrderByTaskTypeAscSortOrderAsc(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DailyTaskResponse getDailyTaskById(Long id) {
        AppUser currentUser = getCurrentUser();

        Task task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        return toResponse(task);
    }

    public DailyTaskResponse createDailyTask(CreateDailyTaskRequest request) {
        AppUser currentUser = getCurrentUser();

        Task task = new Task(
                request.title(),
                request.description(),
                request.difficulty(),
                request.taskType()
        );

        task.setUser(currentUser);
        Long taskCount = dailyTaskRepository.countByUserIdAndTaskType(
                currentUser.getId(),
                request.taskType()
        );
        task.setSortOrder(taskCount.intValue());

        if (request.taskType() == TaskType.TODO) {
            task.setDueDate(request.dueDate());
        } else {
            task.setDueDate(null);
        }

        Task savedTask = dailyTaskRepository.save(task);

        return toResponse(savedTask);
    }

    public DailyTaskResponse updateDailyTask(Long id, CreateDailyTaskRequest request) {
        AppUser currentUser = getCurrentUser();

        Task task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
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

        Task updatedTask = dailyTaskRepository.save(task);

        return toResponse(updatedTask);
    }

    public void deleteDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        Task task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        dailyTaskRepository.delete(task);
    }

    // Implement the updateSortOrder method
    public List<DailyTaskResponse> updateSortOrder(
            TaskType taskType,
            List<Long> taskIds
    ) {
        System.out.println(taskType);
        System.out.println(taskIds);
        
        AppUser currentUser = getCurrentUser();

        for (int i = 0; i < taskIds.size(); i++) {
            Long taskId = taskIds.get(i);

            Task task = dailyTaskRepository
                    .findByIdAndUserId(taskId, currentUser.getId())
                    .orElseThrow(() -> new DailyTaskNotFoundException(taskId));

            task.setSortOrder(i);
            dailyTaskRepository.save(task);
        }

        return dailyTaskRepository
                .findByUserIdAndTaskTypeOrderBySortOrderAsc(
                        currentUser.getId(),
                        taskType
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public DailyTaskResponse completeDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        Task task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));
        
        // XP earned when completing the task
        int gainedXp = task.getBaseXp();
        
        // Coins earned when completing the task
        int gainedCoins = task.getDifficulty().getCoinReward();

        if (task.getTaskType() == TaskType.HABIT) {
            int currentCount = getSafeCompletedCount(task);
            task.setCompletedCount(currentCount + 1);

            addXp(currentUser, gainedXp);
            addCoins(currentUser, gainedCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

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
            addCoins(currentUser, gainedCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.TODO) {
            if (!task.getActive()) {
                throw new TaskAlreadyCompletedException(id);
            }

            task.setActive(false);

            addXp(currentUser, gainedXp);
            addCoins(currentUser, gainedCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        return toResponse(task);
    }

    public DailyTaskResponse revertDailyTask(Long id) {
        AppUser currentUser = getCurrentUser();

        Task task = dailyTaskRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new DailyTaskNotFoundException(id));

        // XP removed when reverting the task
        int lostXp = task.getBaseXp();

        // Coins removed when reverting the task
        int lostCoins = task.getDifficulty().getCoinReward();

        if (task.getTaskType() == TaskType.HABIT) {
            int currentCount = getSafeCompletedCount(task);

            if (currentCount <= 0) {
                return toResponse(task);
            }

            task.setCompletedCount(currentCount - 1);
            removeXp(currentUser, lostXp);
            removeCoins(currentUser, lostCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

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
            removeCoins(currentUser, lostCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        if (task.getTaskType() == TaskType.TODO) {
            if (task.getActive()) {
                return toResponse(task);
            }

            task.setActive(true);
            removeXp(currentUser, lostXp);
            removeCoins(currentUser, lostCoins);

            appUserRepository.save(currentUser);
            Task updatedTask = dailyTaskRepository.save(task);

            return toResponse(updatedTask);
        }

        return toResponse(task);
    }

    private int getSafeCompletedCount(Task task) {
        return task.getCompletedCount() == null ? 0 : task.getCompletedCount();
    }

    private void addXp(AppUser user, int xp) {
        int newTotalXp = user.getTotalXp() + xp;

        user.setTotalXp(newTotalXp);
        user.setLevel(calculateLevel(newTotalXp));
    }

    private void removeXp(AppUser user, int xp) {
        int newTotalXp = Math.max(0, user.getTotalXp() - xp);

        user.setTotalXp(newTotalXp);
        user.setLevel(calculateLevel(newTotalXp));
    }

    // Add coins to the user's balance
    private void addCoins(AppUser user, int coins) {
        user.setCoinBalance(user.getCoinBalance() + coins);
    }

    
    private void removeCoins(AppUser user, int coins) {
        int newCoinBalance = Math.max(0, user.getCoinBalance() - coins);
        user.setCoinBalance(newCoinBalance);
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

    private int calculateLevel(int totalXp) {
        int level = 1;
        int remainingXp = totalXp;

        while (remainingXp >= getRequiredXpForLevel(level)) {
            remainingXp -= getRequiredXpForLevel(level);
            level++;
        }

        return level;
    }

    private int getRequiredXpForLevel(int level) {
        return 100 + (level - 1) * 50;
    }

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private DailyTaskResponse toResponse(Task task) {
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
                task.getDueDate(),
                task.getSortOrder(),
                task.getSubTasks().stream()
                    .map(subTask -> new SubTaskResponse(
                        subTask.getId(),
                        subTask.getTitle(),
                        subTask.isCompleted()
                    ))
                    .toList()
        );
    }
}
