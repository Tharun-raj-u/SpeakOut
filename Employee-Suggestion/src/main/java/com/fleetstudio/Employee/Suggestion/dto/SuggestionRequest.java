package com.fleetstudio.Employee.Suggestion.dto;

import lombok.Data;

@Data
public class SuggestionRequest {
    private String title;
    private String description;
    private boolean anonymous;
}
