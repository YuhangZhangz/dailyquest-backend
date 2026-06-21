package com.example.dailyquest.model;

import jakarta.persistence.*;

// Task
//   └── sub_task
//          id
//          title
//          completed
//          daily_task_id
// One Task can have many SubTasks.
// Each SubTask belongs to one Task through daily_task_id.

@Entity
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;

    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_task_id", nullable = false)
    private Task dailyTask;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public Task getDailyTask() {
        return dailyTask;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setDailyTask(Task dailyTask) {
        this.dailyTask = dailyTask;
    }

}
