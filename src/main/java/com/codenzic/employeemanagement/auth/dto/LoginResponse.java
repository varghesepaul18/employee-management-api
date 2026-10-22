package com.codenzic.employeemanagement.auth.dto;

public record LoginResponse(
    String accessToken, 
    String tokenType, 
    long expiresIn) {

}
