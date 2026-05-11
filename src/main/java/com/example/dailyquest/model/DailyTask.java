package com.example.dailyquest.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Integer baseXp;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DailyTask() {
        // Default constructor for JPA
    }

    public DailyTask(String title, String description, Difficulty difficulty) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
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

    public Integer getBaseXp() {
        return baseXp;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void setBaseXp(Integer baseXp) {
        this.baseXp = baseXp;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
