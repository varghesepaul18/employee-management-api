package com.codenzic.employeemanagement.employee.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.service.EmployeeService;


@PreAuthorize("hasRole('ADMIN')")
@RestController
@Slf4j
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        log.info("Received request to create an employee");

        EmployeeResponse createdEmployee = employeeService.createEmployee(employeeRequest);
        log.info("Created employee with ID {}", createdEmployee.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        log.info("Received request to retrieve employee with ID {}", id);

        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("Received request to retrieve all employees");
        
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest employeeRequest
    ) {

        log.info("Received request to update employee with ID {}", id);
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        log.info("Updated employee with ID {}", updatedEmployee.id());

        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {

        log.info("Received request to delete employee with ID {}", id);
        employeeService.deleteEmployee(id);
        log.info("Deleted employee with ID {}", id);

    }

}
