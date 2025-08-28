package com.fleetstudio.Employee.Suggestion.security.sign.dto;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private  String message;


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
