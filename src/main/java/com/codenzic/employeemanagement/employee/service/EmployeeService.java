package com.codenzic.employeemanagement.employee.service;

import java.util.List;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest employeeRequest);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest);

    void deleteEmployee(Long id);

}
