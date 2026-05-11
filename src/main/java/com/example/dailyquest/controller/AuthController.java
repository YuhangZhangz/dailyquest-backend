package com.example.dailyquest.controller;

import com.example.dailyquest.dto.request.RegisterRequest;
import com.example.dailyquest.dto.request.LoginRequest;
import com.example.dailyquest.dto.response.AuthResponse;
import com.example.dailyquest.service.AuthService;
import com.example.dailyquest.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserProfileResponse getCurrentUserProfile() {
        return authService.getCurrentUserProfile();
    }
}