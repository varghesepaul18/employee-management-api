package com.codenzic.employeemanagement.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.codenzic.employeemanagement.auth.dto.LoginRequest;
import com.codenzic.employeemanagement.auth.dto.LoginResponse;
import com.codenzic.employeemanagement.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController 
@Slf4j 
@RequestMapping ("/api/v1/auth")
@Tag(name = "Authentication", description = "Obtain a JWT access token")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticate with the configured admin credentials and receive a JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login succeeded"),
            @ApiResponse(responseCode = "400", description = "Request validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for username: {}", loginRequest.username());
        return authService.login(loginRequest);
    }

}
