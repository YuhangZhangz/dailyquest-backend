package com.example.dailyquest.repository;

import com.example.dailyquest.model.Task;
import com.example.dailyquest.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdOrderBySortOrderAsc(Long userId);

    List<Task> findByUserIdOrderByTaskTypeAscSortOrderAsc(Long userId);

    List<Task> findByUserIdAndTaskTypeOrderBySortOrderAsc(Long userId, TaskType taskType);

    Long countByUserId(Long userId);

    Long countByUserIdAndTaskType(Long userId, TaskType taskType);

    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
