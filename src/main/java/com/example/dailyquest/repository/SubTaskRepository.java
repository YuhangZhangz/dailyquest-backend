package com.example.dailyquest.repository;

import com.example.dailyquest.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    
}

