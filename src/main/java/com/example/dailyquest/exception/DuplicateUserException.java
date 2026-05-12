package com.example.dailyquest.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String email) {
        super("User with email already exists: " + email);
    }
    
}
