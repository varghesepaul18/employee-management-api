package com.codenzic.employeemanagement.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "employees")
public class Employee {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column (name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column (name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column (name = "department", nullable = false, length = 100)
    private String department;

    @Column (name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", nullable = false, length = 20)
    private EmployeeStatus status;

    //reqired by JPA
    protected Employee() {
    }

    public Employee(String firstName, String lastName, String email, String department, String jobTitle, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

}
