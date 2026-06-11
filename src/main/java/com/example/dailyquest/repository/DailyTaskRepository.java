package com.example.dailyquest.repository;

import com.example.dailyquest.model.DailyTask;
import com.example.dailyquest.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {
    List<DailyTask> findByUserId(Long userId);

    List<DailyTask> findByUserIdOrderBySortOrderAsc(Long userId);

    List<DailyTask> findByUserIdOrderByTaskTypeAscSortOrderAsc(Long userId);

    List<DailyTask> findByUserIdAndTaskTypeOrderBySortOrderAsc(Long userId, TaskType taskType);

    Long countByUserId(Long userId);

    Long countByUserIdAndTaskType(Long userId, TaskType taskType);

    Optional<DailyTask> findByIdAndUserId(Long id, Long userId);
}