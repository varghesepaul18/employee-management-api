package com.codenzic.employeemanagement.employee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codenzic.employeemanagement.employee.entity.Employee;
import com.codenzic.employeemanagement.employee.entity.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
            SELECT employee
            FROM Employee employee
            WHERE (:status IS NULL OR employee.status = :status)
              AND (
                    :search = ''
                    OR LOWER(employee.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(employee.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(employee.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(employee.department) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(employee.jobTitle) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """)
    Page<Employee> searchEmployees(
            @Param("search") String search,
            @Param("status") EmployeeStatus status,
            Pageable pageable);

}
