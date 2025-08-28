package com.fleetstudio.Employee.Suggestion.service;

import lombok.Data;

@Data
public class SuggestionRequest {
    private String title;
    private String description;
    private boolean anonymous;
}
