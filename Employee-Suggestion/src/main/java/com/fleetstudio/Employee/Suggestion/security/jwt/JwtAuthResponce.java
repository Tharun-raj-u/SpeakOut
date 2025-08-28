package com.fleetstudio.Employee.Suggestion.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

public class JwtAuthResponce {
    private String token;

    public String getToken() {
        return token;
    }

    public JwtAuthResponce setToken(String token) {
        this.token = token;
        return this;
    }

    public JwtAuthResponce(String token) {
        this.token = token;
    }
}
