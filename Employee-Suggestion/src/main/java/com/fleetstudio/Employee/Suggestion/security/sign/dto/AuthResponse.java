package com.fleetstudio.Employee.Suggestion.security.sign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;

    public String getToken() {
        return token;
    }

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
