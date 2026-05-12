package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.LoginRequest;
import com.example.dailyquest.dto.request.RegisterRequest;
import com.example.dailyquest.dto.response.AuthResponse;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.dailyquest.dto.response.UserProfileResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.dailyquest.exception.InvalidCredentialsException;
import com.example.dailyquest.exception.DuplicateUserException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already exists: " + request.email());
        }

        if (appUserRepository.existsByUsername(request.username())) {
            throw new DuplicateUserException("Username already exists: " + request.username());
        }

        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        AppUser savedUser = appUserRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jwtService.generateToken(user.getId(), user.getEmail())
                
        );
    }

    public UserProfileResponse getCurrentUserProfile() {
        AppUser currentUser = (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return new UserProfileResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getTotalXp(),
                currentUser.getLevel(),
                currentUser.getDailyStreak()
        );
    }
}