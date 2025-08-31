package com.fleetstudio.Employee.Suggestion.security.sign.dto;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private  String message;

    public LoginResponse(String message, String token, String role) {
        this.message = message;
        this.token = token;
        this.role=role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;


    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public LoginResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }
}
