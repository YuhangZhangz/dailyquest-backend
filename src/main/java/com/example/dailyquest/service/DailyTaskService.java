package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateDailyTaskRequest;
import com.example.dailyquest.dto.response.DailyTaskResponse;
import com.example.dailyquest.exception.DailyTaskNotFoundException;
import com.example.dailyquest.model.DailyTask;
import com.example.dailyquest.repository.DailyTaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DailyTaskService {

    private final DailyTaskRepository dailyTaskRepository;

    public DailyTaskService(DailyTaskRepository dailyTaskRepository) {
        this.dailyTaskRepository = dailyTaskRepository;
    }

    public List<DailyTaskResponse> getAllDailyTasks() {
        return dailyTaskRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public DailyTaskResponse getDailyTaskById(Long id) {
        DailyTask task = dailyTaskRepository.findById(id)
            .orElseThrow(() -> new DailyTaskNotFoundException(id));
        
            return toResponse(task);
    }

    public DailyTaskResponse updateDailyTask(Long id, CreateDailyTaskRequest request) {
        DailyTask task = dailyTaskRepository.findById(id)
            .orElseThrow(() -> new DailyTaskNotFoundException(id));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDifficulty(request.difficulty());

        DailyTask updatedTask = dailyTaskRepository.save(task);

        return toResponse(updatedTask);
    }

    public DailyTaskResponse createDailyTask(CreateDailyTaskRequest request) {
        DailyTask task = new DailyTask(
            request.title(),
            request.description(),
            request.difficulty()
        );

        DailyTask savedTask = dailyTaskRepository.save(task);

        return toResponse(savedTask);
    }

    public void deleteDailyTask(Long id) {
        if (!dailyTaskRepository.existsById(id)) {
            throw new RuntimeException("Daily Task not found with id: " + id);
        }
        dailyTaskRepository.deleteById(id);
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