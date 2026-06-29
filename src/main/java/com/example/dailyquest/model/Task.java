package com.example.dailyquest.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_task")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "growth_category", nullable = false, length = 30)
    private GrowthCategory growthCategory = GrowthCategory.NONE;

    @Column(nullable = false)
    private Integer baseXp;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Integer sortOrder = 0;

    // Track habit completion count for habit tasks
    @Column(nullable = false)
    private Integer completedCount = 0;

    // Track when a daily task was last completed
    private LocalDate lastCompletedDate;
    
    // Optional due date for todo tasks
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @OneToMany(mappedBy = "dailyTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    private List<SubTask> subTasks = new ArrayList<>();

    public Task() {
        // Default constructor for JPA
    }

    public Task(String title, String description, Difficulty difficulty, TaskType taskType) {
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

    public GrowthCategory getGrowthCategory() {
        return growthCategory;
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

    public Integer getSortOrder() {
        return sortOrder;
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
    
    // Optional due date methods
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public List<SubTask> getSubTasks() {
        return subTasks;
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

    public void setGrowthCategory(GrowthCategory growthCategory) {
        this.growthCategory = growthCategory;
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

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    // Optional due date methods
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

}
