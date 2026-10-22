package com.codenzic.employeemanagement.employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.dto.EmployeeSortField;
import com.codenzic.employeemanagement.employee.dto.PageResponse;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;
import com.codenzic.employeemanagement.employee.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Test
    void getAllEmployeesPassesFiltersAndPagingToService() {
        var controller = new EmployeeController(employeeService);
        var expectedPageable = PageRequest.of(
                1,
                5,
                Sort.by(Sort.Direction.DESC, "lastName")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        );
        var expectedResponse = new PageResponse<EmployeeResponse>(
                List.of(), 1, 5, 0, 0, false, true
        );

        when(employeeService.getEmployees("engineer", EmployeeStatus.ACTIVE, expectedPageable))
                .thenReturn(expectedResponse);

        var result = controller.getAllEmployees(
                "engineer",
                EmployeeStatus.ACTIVE,
                1,
                5,
                EmployeeSortField.LAST_NAME,
                Sort.Direction.DESC
        );

        assertEquals(200, result.getStatusCode().value());
        assertEquals(expectedResponse, result.getBody());
        verify(employeeService).getEmployees("engineer", EmployeeStatus.ACTIVE, expectedPageable);
    }
}
