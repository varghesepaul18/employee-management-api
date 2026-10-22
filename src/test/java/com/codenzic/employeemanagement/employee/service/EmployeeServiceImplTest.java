package com.codenzic.employeemanagement.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.codenzic.employeemanagement.employee.dto.EmployeeResponse;
import com.codenzic.employeemanagement.employee.entity.Employee;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;
import com.codenzic.employeemanagement.employee.mapper.EmployeeMapper;
import com.codenzic.employeemanagement.employee.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void getEmployeesTrimsSearchMapsContentAndReturnsPageMetadata() {
        var pageable = PageRequest.of(
                1,
                2,
                Sort.by(Sort.Direction.DESC, "lastName")
        );
        var employee = new Employee(
                "Anna", "Thomas", "anna@example.com", "Engineering", "Developer", EmployeeStatus.ACTIVE
        );
        var response = new EmployeeResponse(
                3L, "Anna", "Thomas", "anna@example.com", "Engineering", "Developer", EmployeeStatus.ACTIVE
        );
        var employeePage = new PageImpl<>(List.of(employee), pageable, 5);

        when(employeeRepository.searchEmployees("anna", EmployeeStatus.ACTIVE, pageable))
                .thenReturn(employeePage);
        when(employeeMapper.toResponse(employee)).thenReturn(response);

        var result = employeeService.getEmployees(" anna ", EmployeeStatus.ACTIVE, pageable);

        assertEquals(List.of(response), result.content());
        assertEquals(1, result.page());
        assertEquals(2, result.size());
        assertEquals(5, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(false, result.first());
        assertEquals(false, result.last());
        verify(employeeRepository).searchEmployees("anna", EmployeeStatus.ACTIVE, pageable);
    }

    @Test
    void getEmployeesUsesNullSearchWhenSearchIsBlank() {
        var pageable = PageRequest.of(0, 10);
        when(employeeRepository.searchEmployees("", null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        var result = employeeService.getEmployees("  ", null, pageable);

        assertEquals(List.of(), result.content());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertEquals(true, result.first());
        assertEquals(true, result.last());
        verify(employeeRepository).searchEmployees("", null, pageable);
    }
}
