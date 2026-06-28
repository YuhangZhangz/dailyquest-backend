package com.example.dailyquest.repository;

import com.example.dailyquest.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    Long countByDailyTaskId(Long dailyTaskId);

    List<SubTask> findByDailyTaskIdOrderBySortOrderAsc(Long dailyTaskId);
}

