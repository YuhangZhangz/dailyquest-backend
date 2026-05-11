package com.example.dailyquest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Store the hashed password instead of the plain text password, example: $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3r0uJ8m
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Integer totalXp = 0; // Track total XP earned by the user

    @Column(nullable = false)
    private Integer level = 1; // Track user level based on XP

    @Column(nullable = false)
    private Integer dailyStreak = 0; // Track the user's current daily streak

    public AppUser() {}

    public AppUser(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.totalXp = 0;
        this.level = 1;
        this.dailyStreak = 0;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Integer getTotalXp() {
        return totalXp;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getDailyStreak() {
        return dailyStreak;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setDailyStreak(Integer dailyStreak) {
        this.dailyStreak = dailyStreak;
    }
    
}
