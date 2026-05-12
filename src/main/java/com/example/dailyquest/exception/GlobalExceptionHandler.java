package com.example.dailyquest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DailyTaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDailyTaskNotFound(DailyTaskNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TaskAlreadyCompletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTaskAlreadyCompleted(TaskAlreadyCompletedException ex) {
        return ex.getMessage();
    }

}