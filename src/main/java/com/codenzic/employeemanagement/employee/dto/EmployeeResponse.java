package com.codenzic.employeemanagement.employee.dto;

import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;

public record EmployeeResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String department,
    String jobTitle,
    EmployeeStatus status
) {

}
