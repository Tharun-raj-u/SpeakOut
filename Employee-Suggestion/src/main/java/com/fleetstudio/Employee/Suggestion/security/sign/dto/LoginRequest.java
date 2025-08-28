package com.fleetstudio.Employee.Suggestion.security.sign.dto;


import lombok.Data;

@Data
public class LoginRequest {


    private String email;


    private String password;

    public String getPassword() {
        return password;
    }

    public LoginRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public LoginRequest setEmail(String email) {
        this.email = email;
        return this;
    }
}
