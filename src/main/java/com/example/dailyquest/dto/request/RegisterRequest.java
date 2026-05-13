package com.example.dailyquest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "Username is required")
    String username,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    // Password must be at least 8 characters, 
    // contain at least one uppercase letter, 
    // one lowercase letter, one digit, 
    // and one special character. It should not contain sequences 
    // of 4 or more repeated characters or common sequences like 
    // "123", "234", etc.
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])(?!.*(.)\\1{3,})(?!.*(?:012|123|234|345|456|567|678|789)).+$",
        message = "Password does not meet security requirements"
    )
    String password
) {
}