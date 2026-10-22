package com.codenzic.employeemanagement.employee.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.codenzic.employeemanagement.employee.dto.EmployeeRequest;
import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.dto.EmployeeSortField;
import com.codenzic.employeemanagement.employee.dto.PageResponse;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;
import com.codenzic.employeemanagement.employee.service.EmployeeService;



@PreAuthorize("hasRole('ADMIN')")
@RestController
@Slf4j
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Admin-only employee management operations")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Request validation failed"),
            @ApiResponse(responseCode = "409", description = "Email is already in use")
    })
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        log.info("Received request to create an employee");

        EmployeeResponse createdEmployee = employeeService.createEmployee(employeeRequest);
        log.info("Created employee with ID {}", createdEmployee.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EmployeeResponse> getEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long id
    ) {
        log.info("Received request to retrieve employee with ID {}", id);

        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/all")
    @Operation(summary = "List employees with pagination and optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<EmployeeResponse>> getAllEmployees(
            @Parameter(description = "Search name, email, department, or job title")
            @RequestParam(required = false)
            @Size(max = 100)
            String search,

            @Parameter(description = "Filter by employee status")
            @RequestParam(required = false)
            EmployeeStatus status,

            @Parameter(description = "Zero-based page number")
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @Parameter(description = "Number of employees per page; maximum 100")
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "LAST_NAME")
            EmployeeSortField sortBy,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "ASC")
            Sort.Direction direction
    ) {
        log.info("Received employee list request for page {}", page);

        Sort sort = Sort.by(direction, sortBy.getProperty())
                .and(Sort.by(Sort.Direction.ASC, "id"));

        var pageable = PageRequest.of(page, size, sort);

        PageResponse<EmployeeResponse> employees =
                employeeService.getEmployees(search, status, pageable);

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace employee details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated"),
            @ApiResponse(responseCode = "400", description = "Request validation failed"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Email is already in use")
    })
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest employeeRequest
    ) {

        log.info("Received request to update employee with ID {}", id);
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        log.info("Updated employee with ID {}", updatedEmployee.id());

        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public void deleteEmployee(@Parameter(description = "Employee ID") @PathVariable Long id) {

        log.info("Received request to delete employee with ID {}", id);
        employeeService.deleteEmployee(id);
        log.info("Deleted employee with ID {}", id);
    }

}
