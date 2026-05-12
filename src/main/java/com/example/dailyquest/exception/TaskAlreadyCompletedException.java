package com.example.dailyquest.exception;

public class TaskAlreadyCompletedException extends RuntimeException {
    public TaskAlreadyCompletedException(Long id) {
        super("Daily task already completed with id: " + id);
    }
}