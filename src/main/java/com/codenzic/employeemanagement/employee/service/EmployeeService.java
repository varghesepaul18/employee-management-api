package com.codenzic.employeemanagement.employee.service;

import org.springframework.data.domain.Pageable;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.dto.PageResponse;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest employeeRequest);

    PageResponse<EmployeeResponse> getEmployees(
            String search,
            EmployeeStatus status,
            Pageable pageable);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest);

    void deleteEmployee(Long id);

}
