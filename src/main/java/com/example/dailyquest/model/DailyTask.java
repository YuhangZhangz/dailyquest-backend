package com.example.dailyquest.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
public class DailyTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Column(nullable = false)
    private Integer baseXp;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    // Track habit completion count for habit tasks
    @Column(nullable = false)
    private Integer completedCount = 0;

    // Track when a daily task was last completed
    private LocalDate lastCompletedDate;

    public DailyTask() {
        // Default constructor for JPA
    }

    public DailyTask(String title, String description, Difficulty difficulty, TaskType taskType) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.taskType = taskType;
        this.baseXp = difficulty.getBaseXp();
        this.active = true; // Default to active when created
        this.createdAt = LocalDateTime.now(); // Set creation time
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public TaskType getTaskType() {
        return taskType;
    }
    
    public Integer getBaseXp() {
        return baseXp;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AppUser getUser() {
        return user;
    }
    
    // Habit completion count methods
    public Integer getCompletedCount() {
        return completedCount;
    }

    // Daily task last completed date methods
    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setBaseXp(Integer baseXp) {
        this.baseXp = baseXp;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
    
    // Habit completion count methods
    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    // Track the last completed date for Daily tasks
    public void setLastCompletedDate(LocalDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

}
