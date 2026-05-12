package com.example.dailyquest.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String massage) {
        super(massage);
    }
    
}
