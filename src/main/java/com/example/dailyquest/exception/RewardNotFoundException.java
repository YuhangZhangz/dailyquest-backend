package com.example.dailyquest.exception;

public class RewardNotFoundException extends RuntimeException {

    public RewardNotFoundException(Long id) {
        super("Reward not found with id: " + id);
    }
}
