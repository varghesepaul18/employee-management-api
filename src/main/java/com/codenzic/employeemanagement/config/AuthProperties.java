package com.codenzic.employeemanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth.admin")
public record AuthProperties(String username, String password) {
}
