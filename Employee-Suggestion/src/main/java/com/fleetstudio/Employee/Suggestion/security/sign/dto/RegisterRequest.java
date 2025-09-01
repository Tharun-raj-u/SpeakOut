package com.fleetstudio.Employee.Suggestion.security.sign.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;        // Matches Employee.name
    private String email;       // Matches Employee.email
    private String password;    // Matches Employee.password
    private String department;  // Matches Employee.department
    private String position;    // Matches Employee.position
    private String role;        // Matches Employee.role (single role instead of Set<String>)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
