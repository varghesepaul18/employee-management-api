package com.codenzic.employeemanagement.employee.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.codenzic.employeemanagement.employee.entity.Employee;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void searchEmployeesMatchesTextAndStatusTogether() {
        String searchTerm = "repo-search-" + UUID.randomUUID();
        employeeRepository.saveAndFlush(employee(
                "Anna", "Thomas", "anna." + searchTerm + "@example.com", searchTerm, EmployeeStatus.ACTIVE
        ));
        employeeRepository.saveAndFlush(employee(
                "Ben", "George", "ben." + searchTerm + "@example.com", searchTerm, EmployeeStatus.INACTIVE
        ));
        employeeRepository.saveAndFlush(employee(
                "Chris", "Paul", "chris.other@example.com", "Other", EmployeeStatus.ACTIVE
        ));

        var result = employeeRepository.searchEmployees(
                searchTerm.toUpperCase(),
                EmployeeStatus.ACTIVE,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("anna." + searchTerm + "@example.com", result.getContent().getFirst().getEmail());
    }

    @Test
    void searchEmployeesReturnsRequestedPageAndMatchingTotal() {
        String searchTerm = "repo-page-" + UUID.randomUUID();
        employeeRepository.saveAndFlush(employee(
                "Anna", "Thomas", "anna." + searchTerm + "@example.com", searchTerm, EmployeeStatus.ACTIVE
        ));
        employeeRepository.saveAndFlush(employee(
                "Ben", "George", "ben." + searchTerm + "@example.com", searchTerm, EmployeeStatus.ACTIVE
        ));
        employeeRepository.saveAndFlush(employee(
                "Chris", "Paul", "chris." + searchTerm + "@example.com", searchTerm, EmployeeStatus.INACTIVE
        ));

        var result = employeeRepository.searchEmployees(
                searchTerm,
                EmployeeStatus.ACTIVE,
                PageRequest.of(1, 1, Sort.by(Sort.Direction.ASC, "id"))
        );

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }

    private Employee employee(
            String firstName,
            String lastName,
            String email,
            String department,
            EmployeeStatus status
    ) {
        return new Employee(firstName, lastName, email, department, "Developer", status);
    }
}
