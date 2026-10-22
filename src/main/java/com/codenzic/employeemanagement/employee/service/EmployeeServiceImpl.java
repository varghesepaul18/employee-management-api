package com.codenzic.employeemanagement.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.mapper.EmployeeMapper;
import com.codenzic.employeemanagement.employee.repository.EmployeeRepository;
import com.codenzic.employeemanagement.exception.DuplicateResourceException;
import com.codenzic.employeemanagement.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;


@Service 
@Slf4j 
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }



    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) {
        log.info("Creating a new employee");

        if (employeeRepository.existsByEmail(employeeRequest.email())) {
            throw new DuplicateResourceException("An employee with that email already exists.");
        }

        var employee = employeeMapper.toEntity(employeeRequest);
        var savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employees");

        var employees = employeeRepository.findAll();
        return employees.stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found."));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest) {
        log.info("Updating employee with ID: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found."));

        if (employeeRepository.existsByEmailAndIdNot(employeeRequest.email(), id)) {
            throw new DuplicateResourceException("An employee with that email already exists.");
        }

        employeeMapper.updateEntity(employee, employeeRequest);

        var updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);

        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee with ID " + id + " not found.");
        }
        employeeRepository.deleteById(id);
    }
}
