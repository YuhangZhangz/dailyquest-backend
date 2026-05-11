package com.example.dailyquest.exception;

public class DailyTaskNotFoundException extends RuntimeException {

    public DailyTaskNotFoundException(Long id) {
        super("Daily task not found: " + id);
    }
}