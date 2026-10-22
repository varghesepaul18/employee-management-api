package com.codenzic.employeemanagement.employee.dto;

public enum EmployeeSortField {
    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    DEPARTMENT("department"),
    JOB_TITLE("jobTitle"),
    STATUS("status");

    private final String property;

    EmployeeSortField(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}