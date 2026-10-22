package com.codenzic.employeemanagement.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codenzic.employeemanagement.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

}
