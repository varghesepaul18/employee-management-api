package com.codenzic.employeemanagement.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codenzic.employeemanagement.auth.dto.LoginRequest;
import com.codenzic.employeemanagement.auth.dto.LoginResponse;
import com.codenzic.employeemanagement.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController 
@Slf4j 
@RequestMapping ("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for username: {}", loginRequest.username());
        return authService.login(loginRequest);
    }

}
