package com.example.dailyquest.repository;

import com.example.dailyquest.model.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {
}