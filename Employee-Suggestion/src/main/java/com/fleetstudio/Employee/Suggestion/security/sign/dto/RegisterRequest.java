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

}
