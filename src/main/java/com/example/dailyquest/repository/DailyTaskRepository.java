package com.example.dailyquest.repository;

import com.example.dailyquest.model.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {
    List<DailyTask> findByUserId(Long userId);
    Optional<DailyTask> findByIdAndUserId(Long id, Long userId);
}