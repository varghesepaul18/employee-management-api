package com.codenzic.employeemanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank 
    String username, 
    @NotBlank 
    String password) {

}
