package com.codenzic.employeemanagement.employee.mapper;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return new Employee(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.department(),
                request.jobTitle(),
                request.status());
    }

    public Employee updateEntity(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setDepartment(request.department());
        employee.setJobTitle(request.jobTitle());
        employee.setStatus(request.status());
        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getStatus());
    }
}
